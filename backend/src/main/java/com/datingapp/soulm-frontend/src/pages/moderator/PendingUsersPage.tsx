import { useInfiniteQuery } from '@tanstack/react-query';
import { EmptyState, ErrorState, LoadingState, LoadMore } from '@/components/common/StateViews';
import { ModerationActions } from '@/components/staff/ModerationActions';
import { adminName, UserReviewCard } from '@/components/staff/UserReviewCard';
import { listPendingUsers } from '@/features/moderation/moderationApi';
import { usePageTitle } from '@/hooks/usePageTitle';
import { nextPageParam } from '@/utils/format';

export function PendingUsersPage() {
  usePageTitle('People awaiting approval');
  const pending = useInfiniteQuery({
    queryKey: ['moderation', 'pending-users'],
    queryFn: ({ pageParam, signal }) => listPendingUsers({ page: pageParam, size: 20 }, signal),
    initialPageParam: 0,
    getNextPageParam: nextPageParam,
  });
  const items = pending.data?.pages.flatMap((page) => page.content) ?? [];

  return (
    <div className="container page">
      <h1 className="page__title">People awaiting approval</h1>
      <p className="form-note">Includes accounts that were banned before they were ever approved.</p>
      {pending.isPending ? (
        <LoadingState label="Loading" />
      ) : pending.isError ? (
        <ErrorState error={pending.error} onRetry={() => void pending.refetch()} fallback="We couldn't load the list." />
      ) : items.length === 0 ? (
        <EmptyState title="No one is waiting">New sign-ups appear here until a moderator approves them.</EmptyState>
      ) : (
        <>
          <ul className="people">
            {items.map((user) => (
              <UserReviewCard key={user.id} user={user} actions={<ModerationActions user={user} name={adminName(user)} />} />
            ))}
          </ul>
          <LoadMore hasMore={pending.hasNextPage} loading={pending.isFetchingNextPage} onClick={() => void pending.fetchNextPage()} />
        </>
      )}
    </div>
  );
}
