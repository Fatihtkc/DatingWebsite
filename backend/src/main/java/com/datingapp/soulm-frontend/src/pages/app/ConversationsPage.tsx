import { Link, NavLink, useParams } from 'react-router-dom';
import { EmptyState, ErrorState, LoadingState, LoadMore } from '@/components/common/StateViews';
import { ProfileImage } from '@/components/common/ProfileImage';
import { ChatPanel } from '@/features/chat/ChatPanel';
import { approvedImages } from '@/features/media/images';
import { useMatchesQuery } from '@/features/matches/useMatchesQuery';
import { usePageTitle } from '@/hooks/usePageTitle';
import { APP_ROUTES } from '@/routes';
import { cx } from '@/utils/cx';
import { displayName } from '@/utils/format';

/**
 * The backend has no "my conversations" endpoint (no list, previews or unread state), so the list on
 * the left is the list of matches: you can only talk to people you are matched with. A conversation
 * is created on the server the first time it is opened.
 */
export function ConversationsPage() {
  usePageTitle('Messages');
  const { userId } = useParams();
  const otherUserId = userId !== undefined && /^\d+$/.test(userId) ? Number(userId) : null;
  const matches = useMatchesQuery();
  const items = matches.data?.pages.flatMap((page) => page.content) ?? [];

  return (
    <div className={cx('container', 'chat-layout', otherUserId !== null && 'chat-layout--open')}>
      <aside className="chat-list" aria-label="Your matches">
        <h1 className="page__title">Messages</h1>
        {matches.isPending ? (
          <LoadingState label="Loading" />
        ) : matches.isError ? (
          <ErrorState error={matches.error} onRetry={() => void matches.refetch()} fallback="We couldn't load your matches." />
        ) : items.length === 0 ? (
          <EmptyState
            title="No one to message yet"
            action={
              <Link to={APP_ROUTES.discover} className="btn btn--primary">
                Go to Discover
              </Link>
            }
          >
            You can message people once you have matched.
          </EmptyState>
        ) : (
          <>
            <ul className="chat-list__items">
              {items.map((match) => {
                const name = displayName(match.user);
                return (
                  <li key={match.id}>
                    <NavLink to={APP_ROUTES.conversation(match.user.id)} className={({ isActive }) => cx('chat-list__item', isActive && 'is-active')}>
                      <span className="chat__avatar">
                        <ProfileImage src={approvedImages(match.user.images)[0]?.imageUrl} alt="" initial={name} />
                      </span>
                      <span>{name}</span>
                    </NavLink>
                  </li>
                );
              })}
            </ul>
            <LoadMore hasMore={matches.hasNextPage} loading={matches.isFetchingNextPage} onClick={() => void matches.fetchNextPage()} />
          </>
        )}
      </aside>

      <section className="chat-pane" aria-label="Conversation">
        {otherUserId !== null ? (
          <ChatPanel key={otherUserId} otherUserId={otherUserId} />
        ) : (
          <EmptyState title="Choose a conversation">Pick someone from your matches to start talking.</EmptyState>
        )}
      </section>
    </div>
  );
}
