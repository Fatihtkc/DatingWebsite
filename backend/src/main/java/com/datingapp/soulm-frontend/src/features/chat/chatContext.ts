import { createContext } from 'react';
import type { SendMessageRequest } from '@/types/social';
import type { ConnectionState, MessageHandler } from './chatSocket';

export interface ChatContextValue {
  connection: ConnectionState;
  lastError: string | null;
  acquire: () => () => void;
  subscribe: (conversationId: number, handler: MessageHandler) => () => void;
  send: (request: SendMessageRequest) => void;
}

export const ChatContext = createContext<ChatContextValue | null>(null);
