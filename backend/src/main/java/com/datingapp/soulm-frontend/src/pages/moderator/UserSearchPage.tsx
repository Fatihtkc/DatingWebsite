import { useState, type FormEvent } from 'react';
import { useInfiniteQuery } from '@tanstack/react-query';
import { Button } from '@/components/common/Button';
import { EmptyState, ErrorState, LoadingState, LoadMore } from '@/components/common/StateViews';
import { ModerationActions } from '@/components/staff/ModerationActions';
import { adminName, UserReviewCard } from '@/components/staff/UserReviewCard';
import { searchUsers } from '@/features/moderation/moderationApi';
import { usePageTitle } from '@/hooks/usePageTitle';
import { nextPageParam } from '@/utils/format';

export function UserSearchPage() {
  usePageTitle('Find people');
  const [text, setText] = useState('');
  const [query, setQuery] = useState('');

  const results = useInfiniteQuery({
    queryKey: ['moderation', 'search', query],
    queryFn: ({ pageParam, signal }) => searchUsers(query, { page: pageParam, size: 20 }, signal),
    initialPageParam: 0,
    getNextPageParam: nextPageParam,
  });
  const items = results.data?.pages.flatMap((page) => page.content) ?? [];

  function onSubmit(event: FormEvent) {
    event.preventDefault();
    setQuery(text.trim());
  }

  return (
    <div className="container page">
      <h1 className="page__title">Find people</h1>
      <form className="search-bar" role="search" onSubmit={onSubmit}>
        <label className="visually-hidden" htmlFor="user-search">
          Search by first or last name
        </label>
        <input id="user-search" type="search" placeholder="First or last name" value={text} onChange={(event) => setText(event.target.value)} />
        <Button type="submit">Search</Button>
      </form>

      {results.isPending ? (
        <LoadingState label="Searching" />
      ) : results.isError ? (
        <ErrorState error={results.error} onRetry={() => void results.refetch()} fallback="We couldn't search right now." />
      ) : items.length === 0 ? (
        <EmptyState title="No one found">{query ? `Nobody matches "${query}".` : 'There are no members yet.'}</EmptyState>
      ) : (
        <>
          <ul className="people">
            {items.map((user) => (
              <UserReviewCard key={user.id} user={user} actions={<ModerationActions user={user} name={adminName(user)} />} />
            ))}
          </ul>
          <LoadMore hasMore={results.hasNextPage} loading={results.isFetchingNextPage} onClick={() => void results.fetchNextPage()} />
        </>
      )}
    </div>
  );
}
