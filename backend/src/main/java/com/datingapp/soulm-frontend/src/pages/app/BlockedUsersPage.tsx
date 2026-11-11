import { useInfiniteQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { toUserMessage } from '@/api/errors';
import { Alert } from '@/components/common/Alert';
import { Button } from '@/components/common/Button';
import { EmptyState, ErrorState, LoadingState, LoadMore } from '@/components/common/StateViews';
import { PersonCard } from '@/components/dating/PersonCard';
import { listBlockedUsers, unblockUser } from '@/features/blocks/blocksApi';
import { usePageTitle } from '@/hooks/usePageTitle';
import { APP_ROUTES } from '@/routes';
import { nextPageParam } from '@/utils/format';

export function BlockedUsersPage() {
  usePageTitle('Blocked people');
  const queryClient = useQueryClient();
  const blocked = useInfiniteQuery({
    queryKey: ['blocks'],
    queryFn: ({ pageParam, signal }) => listBlockedUsers({ page: pageParam, size: 20 }, signal),
    initialPageParam: 0,
    getNextPageParam: nextPageParam,
  });
  const unblock = useMutation({
    mutationFn: (userId: number) => unblockUser(userId),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['blocks'] });
      void queryClient.invalidateQueries({ queryKey: ['nearby'] });
    },
  });
  const items = blocked.data?.pages.flatMap((page) => page.content) ?? [];

  return (
    <div className="container page page--narrow">
      <div className="page__back">
        <Link to={APP_ROUTES.settings} className="btn btn--ghost">&larr; Settings</Link>
      </div>
      <h1 className="page__title">Blocked people</h1>
      <p className="form-note">Blocked people can&rsquo;t see you, like you or message you. Unblocking does not bring back a match you had.</p>
      {unblock.isError && <Alert tone="error">{toUserMessage(unblock.error, "We couldn't unblock that person.")}</Alert>}

      {blocked.isPending ? (
        <LoadingState label="Loading" />
      ) : blocked.isError ? (
        <ErrorState error={blocked.error} onRetry={() => void blocked.refetch()} fallback="We couldn't load your blocked list." />
      ) : items.length === 0 ? (
        <EmptyState title="You haven't blocked anyone" />
      ) : (
        <>
          <ul className="people">
            {items.map((profile) => (
              <PersonCard
                key={profile.id}
                profile={profile}
                linkToProfile={false}
                actions={
                  <Button variant="secondary" loading={unblock.isPending && unblock.variables === profile.id} onClick={() => unblock.mutate(profile.id)}>
                    Unblock
                  </Button>
                }
              />
            ))}
          </ul>
          <LoadMore hasMore={blocked.hasNextPage} loading={blocked.isFetchingNextPage} onClick={() => void blocked.fetchNextPage()} />
        </>
      )}
    </div>
  );
}
