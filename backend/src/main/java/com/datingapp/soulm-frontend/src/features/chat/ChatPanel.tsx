import { useEffect, useLayoutEffect, useMemo, useRef, useState, type FormEvent, type KeyboardEvent, type ReactNode } from 'react';
import { useInfiniteQuery, useQuery } from '@tanstack/react-query';
import { Link, useNavigate } from 'react-router-dom';
import { ApiError, toUserMessage } from '@/api/errors';
import { getUserProfile } from '@/api/userApi';
import { Alert } from '@/components/common/Alert';
import { Button } from '@/components/common/Button';
import { ProfileImage } from '@/components/common/ProfileImage';
import { EmptyState, LoadingState } from '@/components/common/StateViews';
import { BlockDialog } from '@/components/dating/BlockDialog';
import { ReportDialog } from '@/components/dating/ReportDialog';
import { useAuth } from '@/features/auth/useAuth';
import { approvedImages } from '@/features/media/images';
import { listMessages, openConversation } from '@/features/messages/messagesApi';
import { APP_ROUTES } from '@/routes';
import type { ChatMessage } from '@/types/social';
import { cx } from '@/utils/cx';
import { displayName, formatMessageTime, nextPageParam } from '@/utils/format';
import { expirePending, mergeMessages, settlePending, type PendingMessage } from './messageState';
import { useChat } from './useChat';

const MAX_LENGTH = 2000; // SendMessageDTO @Size(max = 2000)
const SEND_TIMEOUT_MS = 10_000;

function MessageBubble({ mine, time, children, status }: { mine: boolean; time?: string; children: ReactNode; status?: string }) {
  return (
    <li className={cx('bubble', mine ? 'bubble--mine' : 'bubble--theirs')}>
      <div className="bubble__content">{children}</div>
      <span className="bubble__meta">{status ?? time}</span>
    </li>
  );
}

export function ChatPanel({ otherUserId }: { otherUserId: number }) {
  const { state: auth } = useAuth();
  const myId = auth.status === 'authenticated' ? auth.session.userId : -1;
  const chat = useChat();
  const { acquire, subscribe } = chat;

  const profile = useQuery({
    queryKey: ['user', otherUserId],
    queryFn: ({ signal }) => getUserProfile(otherUserId, signal),
    retry: false,
  });
  const conversation = useQuery({
    queryKey: ['conversation', otherUserId],
    queryFn: ({ signal }) => openConversation(otherUserId, signal),
    staleTime: Infinity, // the endpoint is get-or-create, so the id never changes
    retry: false,
  });
  const conversationId = conversation.data ?? null;

  const history = useInfiniteQuery({
    queryKey: ['messages', conversationId],
    queryFn: ({ pageParam, signal }) => listMessages(conversationId ?? 0, { page: pageParam, size: 20 }, signal),
    initialPageParam: 0,
    getNextPageParam: nextPageParam,
    enabled: conversationId !== null,
  });

  const [live, setLive] = useState<ChatMessage[]>([]);
  const [pending, setPending] = useState<PendingMessage[]>([]);
  const [draft, setDraft] = useState('');
  const [notice, setNotice] = useState<string | null>(null);
  const [reporting, setReporting] = useState(false);
  const [blocking, setBlocking] = useState(false);
  const navigate = useNavigate();
  const counter = useRef(0);

  const historyMessages = useMemo(() => history.data?.pages.flatMap((page) => page.content) ?? [], [history.data]);
  const messages = useMemo(() => mergeMessages(historyMessages, live), [historyMessages, live]);

  // Keep the socket open while this view is on screen, and listen to this conversation.
  useEffect(() => acquire(), [acquire]);
  useEffect(() => {
    if (conversationId === null) return;
    return subscribe(conversationId, (message) => setLive((current) => [...current, message]));
  }, [subscribe, conversationId]);

  // A pending message is done once the server's copy appears (from the live topic or from history).
  useEffect(() => {
    setPending((current) => {
      if (current.length === 0) return current;
      const next = settlePending(current, messages, myId);
      return next.length === current.length ? current : next;
    });
  }, [messages, myId]);

  useEffect(() => {
    if (!pending.some((item) => item.status === 'sending')) return;
    const id = window.setInterval(() => setPending((current) => expirePending(current, Date.now(), SEND_TIMEOUT_MS)), 1000);
    return () => window.clearInterval(id);
  }, [pending]);

  // Messages that arrived while the socket was down are not replayed, so reload the latest ones on reconnect.
  const { refetch } = history;
  const hasConnected = useRef(false);
  const previous = useRef(chat.connection);
  useEffect(() => {
    if (chat.connection === 'connected' && previous.current !== 'connected' && hasConnected.current) void refetch();
    if (chat.connection === 'connected') hasConnected.current = true;
    previous.current = chat.connection;
  }, [chat.connection, refetch]);

  // Scrolling: stay at the bottom for new messages, keep position when older ones are loaded above.
  const listRef = useRef<HTMLUListElement>(null);
  const stickToBottom = useRef(true);
  const heightBeforeLoad = useRef<number | null>(null);
  useLayoutEffect(() => {
    const element = listRef.current;
    if (!element) return;
    if (heightBeforeLoad.current !== null) {
      element.scrollTop += element.scrollHeight - heightBeforeLoad.current;
      heightBeforeLoad.current = null;
    } else if (stickToBottom.current) {
      element.scrollTop = element.scrollHeight;
    }
  }, [messages.length, pending.length]);

  function onScroll() {
    const element = listRef.current;
    if (element) stickToBottom.current = element.scrollHeight - element.scrollTop - element.clientHeight < 80;
  }

  function loadEarlier() {
    heightBeforeLoad.current = listRef.current?.scrollHeight ?? null;
    void history.fetchNextPage();
  }

  function send(content: string): boolean {
    const text = content.trim();
    if (text === '' || conversationId === null) return false;
    if (chat.connection !== 'connected') {
      setNotice("You're not connected right now. Your message was not sent.");
      return false;
    }
    try {
      chat.send({ conversationId, content: text, isImage: false });
    } catch {
      setNotice("We couldn't send your message. Please try again.");
      return false;
    }
    setNotice(null);
    counter.current += 1;
    const afterId = messages.at(-1)?.id ?? 0;
    setPending((current) => [...current, { tempId: String(counter.current), content: text, createdAt: Date.now(), afterId, status: 'sending' }]);
    stickToBottom.current = true;
    return true;
  }

  function onSubmit(event: FormEvent) {
    event.preventDefault();
    if (send(draft)) setDraft('');
  }

  function onKeyDown(event: KeyboardEvent<HTMLTextAreaElement>) {
    if (event.key === 'Enter' && !event.shiftKey && !event.nativeEvent.isComposing) {
      event.preventDefault();
      if (send(draft)) setDraft('');
    }
  }

  function retry(item: PendingMessage) {
    setPending((current) => current.filter((candidate) => candidate.tempId !== item.tempId));
    send(item.content);
  }

  if (conversation.isPending) return <LoadingState label="Opening conversation" />;
  if (conversation.isError || profile.isError) {
    const error = conversation.error ?? profile.error;
    const notAvailable = error instanceof ApiError && (error.status === 400 || error.status === 404);
    return (
      <EmptyState title="You can't message this person">
        {notAvailable ? 'You can only message people you are matched with.' : toUserMessage(error, "We couldn't open this conversation.")}
      </EmptyState>
    );
  }

  const name = profile.data ? displayName(profile.data) : 'this person';
  const photo = approvedImages(profile.data?.images)[0];
  const offline = chat.connection !== 'connected';
  const failedCount = pending.filter((item) => item.status === 'failed').length;

  return (
    <div className="chat">
      <header className="chat__header">
        <Link to={APP_ROUTES.conversations} className="chat__back btn btn--ghost" aria-label="Back to messages">
          &larr;
        </Link>
        <Link to={APP_ROUTES.user(otherUserId)} className="chat__who">
          <span className="chat__avatar">
            <ProfileImage src={photo?.imageUrl} alt="" initial={name} />
          </span>
          <span className="chat__name">{name}</span>
        </Link>
        <Button variant="ghost" onClick={() => setReporting(true)}>
          Report
        </Button>
        <Button variant="ghost" onClick={() => setBlocking(true)}>
          Block
        </Button>
      </header>

      {offline && (
        <div className="chat__banner" role="status">
          {chat.connection === 'connecting' ? 'Connecting\u2026' : 'Disconnected. Trying to reconnect\u2026'}
        </div>
      )}

      <ul ref={listRef} className="chat__messages" role="log" aria-label={`Conversation with ${name}`} aria-live="polite" onScroll={onScroll}>
        {history.hasNextPage && (
          <li className="chat__earlier">
            <Button variant="ghost" loading={history.isFetchingNextPage} onClick={loadEarlier}>
              Load earlier messages
            </Button>
          </li>
        )}
        {history.isPending && <li className="chat__earlier"><LoadingState label="Loading messages" /></li>}
        {history.isError && (
          <li className="chat__earlier">
            <Alert tone="error">{toUserMessage(history.error, "We couldn't load the messages.")}</Alert>
            <Button variant="secondary" onClick={() => void history.refetch()}>Try again</Button>
          </li>
        )}
        {history.isSuccess && messages.length === 0 && pending.length === 0 && (
          <li className="chat__earlier">
            <p className="form-note">No messages yet. Say hello to {name}.</p>
          </li>
        )}
        {messages.map((message) => (
          <MessageBubble key={message.id} mine={message.senderId === myId} time={formatMessageTime(message.sentAt)}>
            {message.isImage ? <ProfileImage src={message.content} alt="Photo in the conversation" initial="" className="bubble__image" /> : message.content}
          </MessageBubble>
        ))}
        {pending.map((item) => (
          <MessageBubble key={item.tempId} mine status={item.status === 'sending' ? 'Sending\u2026' : 'Not delivered'}>
            {item.content}
            {item.status === 'failed' && (
              <>
                {' '}
                <button type="button" className="link-button" onClick={() => retry(item)}>
                  Try again
                </button>{' '}
                <button type="button" className="link-button" onClick={() => setPending((c) => c.filter((x) => x.tempId !== item.tempId))}>
                  Dismiss
                </button>
              </>
            )}
          </MessageBubble>
        ))}
      </ul>

      {failedCount > 0 && (
        <p className="chat__hint" role="status">
          Some messages weren&rsquo;t delivered. You may no longer be matched, or the connection dropped.
        </p>
      )}
      {notice && <Alert tone="error">{notice}</Alert>}

      <form className="chat__composer" onSubmit={onSubmit}>
        <label className="visually-hidden" htmlFor="chat-draft">
          Message to {name}
        </label>
        <textarea
          id="chat-draft"
          rows={2}
          maxLength={MAX_LENGTH}
          placeholder="Write a message"
          value={draft}
          onChange={(event) => setDraft(event.target.value)}
          onKeyDown={onKeyDown}
        />
        <Button type="submit" disabled={draft.trim() === '' || offline}>
          Send
        </Button>
      </form>

      <ReportDialog open={reporting} userId={otherUserId} name={name} onClose={() => setReporting(false)} />
      <BlockDialog
        open={blocking}
        userId={otherUserId}
        name={name}
        onClose={() => setBlocking(false)}
        onBlocked={() => navigate(APP_ROUTES.conversations, { replace: true })}
      />
    </div>
  );
}
