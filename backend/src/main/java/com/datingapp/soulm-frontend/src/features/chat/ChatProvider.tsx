import { useEffect, useMemo, useState, useSyncExternalStore, type ReactNode } from 'react';
import { ChatContext, type ChatContextValue } from './chatContext';
import { ChatSocket } from './chatSocket';

/** Owns the STOMP client for the member area. The connection itself opens lazily, when a conversation is shown. */
export function ChatProvider({ children }: { children: ReactNode }) {
  const [socket] = useState(() => new ChatSocket());
  const snapshot = useSyncExternalStore(socket.onChange, socket.getSnapshot);

  useEffect(() => () => socket.shutdown(), [socket]);

  const value = useMemo<ChatContextValue>(
    () => ({
      connection: snapshot.connection,
      lastError: snapshot.lastError,
      acquire: socket.acquire,
      subscribe: socket.subscribe,
      send: socket.send,
    }),
    [snapshot, socket],
  );

  return <ChatContext.Provider value={value}>{children}</ChatContext.Provider>;
}
