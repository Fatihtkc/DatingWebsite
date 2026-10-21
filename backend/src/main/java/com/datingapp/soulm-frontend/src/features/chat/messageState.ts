import type { ChatMessage } from '@/types/social';

/**
 * A message the user has sent that the server has not echoed back yet. The backend answers a
 * send with nothing: it broadcasts the saved message to the conversation topic. Errors raised
 * inside the message handler (blocked, not matched, validation) are never reported to the sender.
 * So a message is "sending" until it shows up as a saved message from the same person.
 */
export interface PendingMessage {
  tempId: string;
  content: string;
  createdAt: number;
  /** Highest message id the client knew when sending; only newer messages can be the echo. */
  afterId: number;
  status: 'sending' | 'failed';
}

/** Union by id (history pages can overlap when new messages arrive), oldest first. */
export function mergeMessages(...lists: ReadonlyArray<readonly ChatMessage[]>): ChatMessage[] {
  const byId = new Map<number, ChatMessage>();
  for (const list of lists) for (const message of list) byId.set(message.id, message);
  return [...byId.values()].sort((a, b) => a.id - b.id);
}

/** Drops pending messages that now exist as saved messages from `myUserId`, one saved message per pending one. */
export function settlePending(pending: readonly PendingMessage[], messages: readonly ChatMessage[], myUserId: number): PendingMessage[] {
  const consumed = new Set<number>();
  return pending.filter((item) => {
    const echo = messages.find(
      (message) =>
        message.senderId === myUserId && message.id > item.afterId && message.content === item.content && !consumed.has(message.id),
    );
    if (echo) {
      consumed.add(echo.id);
      return false;
    }
    return true;
  });
}

/** Marks messages that have waited too long as failed. Returns the same array when nothing changed. */
export function expirePending(pending: PendingMessage[], now: number, timeoutMs: number): PendingMessage[] {
  let changed = false;
  const next = pending.map((item): PendingMessage => {
    if (item.status === 'sending' && now - item.createdAt >= timeoutMs) {
      changed = true;
      return { ...item, status: 'failed' };
    }
    return item;
  });
  return changed ? next : pending;
}
