import { Client, type IMessage, type StompSubscription } from '@stomp/stompjs';
import { z } from 'zod';
import { env } from '@/config/env';
import { getValidAccessToken } from '@/features/auth/sessionManager';
import type { ChatMessage, SendMessageRequest } from '@/types/social';

export type ConnectionState = 'connecting' | 'connected' | 'disconnected';
export type MessageHandler = (message: ChatMessage) => void;

export interface ChatSnapshot {
  connection: ConnectionState;
  lastError: string | null;
}

const chatMessageSchema = z.object({
  id: z.number(),
  conversationId: z.number(),
  senderId: z.number(),
  content: z.string(),
  sentAt: z.string(),
  isImage: z.boolean().nullable(),
  isRead: z.boolean().nullable(),
});

/** SockJS registers a raw WebSocket at <endpoint>/websocket, so sockjs-client is not needed. */
export function toWebSocketUrl(apiBaseUrl: string): string {
  return `${apiBaseUrl.replace(/^http/i, 'ws')}/chat/websocket`;
}

interface Entry {
  handlers: Set<MessageHandler>;
  stomp: StompSubscription | null;
}

/**
 * One STOMP connection for the signed-in session, shared by every conversation view.
 *
 * Contract (WebSocketConfig, StompAuthChannelInterceptor, WebSocketMessageController):
 *  - CONNECT must carry a valid `Authorization: Bearer <jwt>`; the token is checked once, so a fresh
 *    one is fetched before every (re)connect.
 *  - SUBSCRIBE is allowed only to /topic/conversations/{id} and only for members.
 *  - SEND goes to /app/sendMessage, limited to 20 messages per 10 s per user.
 * Interceptor failures come back as STOMP ERROR frames; failures inside the handler are silent.
 */
export class ChatSocket {
  private readonly client: Client;
  private readonly entries = new Map<number, Entry>();
  private readonly listeners = new Set<() => void>();
  private refCount = 0;
  private snapshot: ChatSnapshot = { connection: 'disconnected', lastError: null };

  constructor() {
    this.client = new Client({
      brokerURL: toWebSocketUrl(env.apiBaseUrl),
      reconnectDelay: 4000,
      beforeConnect: async (client) => {
        this.update({ connection: 'connecting' });
        let token: string | null = null;
        try {
          token = await getValidAccessToken();
        } catch {
          // Could not renew (offline). Connect without a token; the server refuses and the normal retry follows.
        }
        client.connectHeaders = token ? { Authorization: `Bearer ${token}` } : {};
      },
      onConnect: () => {
        this.update({ connection: 'connected', lastError: null });
        for (const id of this.entries.keys()) this.ensureSubscribed(id);
      },
      onStompError: (frame) => {
        this.update({ lastError: frame.headers['message'] ?? 'The chat connection reported an error.' });
      },
      onWebSocketClose: () => {
        for (const entry of this.entries.values()) entry.stomp = null; // subscriptions die with the socket
        this.update({ connection: this.client.active ? 'connecting' : 'disconnected' });
      },
    });
  }

  readonly onChange = (listener: () => void): (() => void) => {
    this.listeners.add(listener);
    return () => {
      this.listeners.delete(listener);
    };
  };

  readonly getSnapshot = (): ChatSnapshot => this.snapshot;

  /** Keeps the connection open until the returned function is called. Reference counted. */
  readonly acquire = (): (() => void) => {
    this.refCount += 1;
    if (this.refCount === 1) this.client.activate();
    let released = false;
    return () => {
      if (released) return;
      released = true;
      this.refCount -= 1;
      if (this.refCount === 0) void this.client.deactivate();
    };
  };

  readonly subscribe = (conversationId: number, handler: MessageHandler): (() => void) => {
    let entry = this.entries.get(conversationId);
    if (!entry) {
      entry = { handlers: new Set(), stomp: null };
      this.entries.set(conversationId, entry);
    }
    entry.handlers.add(handler);
    this.ensureSubscribed(conversationId);

    return () => {
      const current = this.entries.get(conversationId);
      if (!current) return;
      current.handlers.delete(handler);
      if (current.handlers.size === 0) {
        try {
          current.stomp?.unsubscribe();
        } catch {
          // the socket is already gone
        }
        this.entries.delete(conversationId);
      }
    };
  };

  /** Throws when not connected. Success only means "handed to the socket", not "saved". */
  readonly send = (request: SendMessageRequest): void => {
    if (!this.client.connected) throw new Error('Not connected.');
    this.client.publish({
      destination: '/app/sendMessage',
      headers: { 'content-type': 'application/json' },
      body: JSON.stringify(request),
    });
  };

  readonly shutdown = (): void => {
    this.refCount = 0;
    void this.client.deactivate();
  };

  private ensureSubscribed(conversationId: number): void {
    const entry = this.entries.get(conversationId);
    if (!entry || entry.stomp || !this.client.connected) return;
    entry.stomp = this.client.subscribe(`/topic/conversations/${conversationId}`, (frame) => this.dispatch(conversationId, frame));
  }

  private dispatch(conversationId: number, frame: IMessage): void {
    let json: unknown;
    try {
      json = JSON.parse(frame.body);
    } catch {
      return;
    }
    const parsed = chatMessageSchema.safeParse(json);
    if (!parsed.success) return;
    for (const handler of this.entries.get(conversationId)?.handlers ?? []) handler(parsed.data);
  }

  private update(patch: Partial<ChatSnapshot>): void {
    this.snapshot = { ...this.snapshot, ...patch };
    for (const listener of this.listeners) listener();
  }
}
