import { useState } from 'react';
import { useInfiniteQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { toUserMessage } from '@/api/errors';
import { Button } from '@/components/common/Button';
import { ConfirmDialog } from '@/components/common/ConfirmDialog';
import { EmptyState, ErrorState, LoadingState, LoadMore } from '@/components/common/StateViews';
import { PersonCard } from '@/components/dating/PersonCard';
import { useAuth } from '@/features/auth/useAuth';
import { listMyLikes, removeLike } from '@/features/likes/likesApi';
import { usePageTitle } from '@/hooks/usePageTitle';
import { APP_ROUTES } from '@/routes';
import type { UserLike } from '@/types/social';
import { displayName, nextPageParam } from '@/utils/format';

export function LikesPage() {
  usePageTitle('Likes');
  const { state } = useAuth();
  const myId = state.status === 'authenticated' ? state.session.userId : null;
  const queryClient = useQueryClient();
  const [target, setTarget] = useState<UserLike | null>(null);

  const likes = useInfiniteQuery({
    queryKey: ['likes', myId],
    queryFn: ({ pageParam, signal }) => listMyLikes(myId ?? 0, { page: pageParam, size: 20 }, signal),
    initialPageParam: 0,
    getNextPageParam: nextPageParam,
    enabled: myId !== null,
  });

  const remove = useMutation({
    mutationFn: (like: UserLike) => removeLike(like.id),
    onSuccess: () => {
      setTarget(null);
      void queryClient.invalidateQueries({ queryKey: ['likes'] });
    },
  });

  const items = likes.data?.pages.flatMap((page) => page.content) ?? [];

  function close() {
    if (!remove.isPending) {
      setTarget(null);
      remove.reset();
    }
  }

  return (
    <div className="container page">
      <h1 className="page__title">People you liked</h1>

      {likes.isPending ? (
        <LoadingState label="Loading your likes" />
      ) : likes.isError ? (
        <ErrorState error={likes.error} onRetry={() => void likes.refetch()} fallback="We couldn't load your likes." />
      ) : items.length === 0 ? (
        <EmptyState
          title="You haven't liked anyone yet"
          action={
            <Link to={APP_ROUTES.discover} className="btn btn--primary">
              Go to Discover
            </Link>
          }
        >
          People you like show up here. If they like you back, you will find them under Matches.
        </EmptyState>
      ) : (
        <>
          <ul className="people">
            {items.map((like) => (
              <PersonCard
                key={like.id}
                profile={like.likedUser}
                detail={`Liked on ${new Date(like.likedAt).toLocaleDateString()}`}
                actions={
                  <Button variant="secondary" onClick={() => setTarget(like)}>
                    Remove like
                  </Button>
                }
              />
            ))}
          </ul>
          <LoadMore hasMore={likes.hasNextPage} loading={likes.isFetchingNextPage} onClick={() => void likes.fetchNextPage()} />
        </>
      )}

      <ConfirmDialog
        open={target !== null}
        title="Remove this like?"
        confirmLabel="Remove like"
        destructive
        busy={remove.isPending}
        error={remove.isError ? toUserMessage(remove.error, "We couldn't remove the like.") : null}
        onConfirm={() => target && remove.mutate(target)}
        onCancel={close}
      >
        {target && (
          <p>
            {displayName(target.likedUser)} won&rsquo;t be told. If you are already matched, removing the like does not end the match; use Unmatch on
            the Matches page for that.
          </p>
        )}
      </ConfirmDialog>
    </div>
  );
}
