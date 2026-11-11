import { useState } from 'react';
import { useInfiniteQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { toUserMessage } from '@/api/errors';
import { Button } from '@/components/common/Button';
import { ConfirmDialog } from '@/components/common/ConfirmDialog';
import { ProfileImage } from '@/components/common/ProfileImage';
import { EmptyState, ErrorState, LoadingState, LoadMore } from '@/components/common/StateViews';
import { approveImage, listPendingImages } from '@/features/moderation/moderationApi';
import { usePageTitle } from '@/hooks/usePageTitle';
import { MODERATOR_ROUTES } from '@/routes';
import type { PendingImage } from '@/types/account';
import { nextPageParam } from '@/utils/format';

function PendingImageRow({ image }: { image: PendingImage }) {
  const queryClient = useQueryClient();
  const [confirming, setConfirming] = useState(false);
  const approve = useMutation({
    mutationFn: () => approveImage(image.id),
    onSuccess: () => {
      setConfirming(false);
      void queryClient.invalidateQueries({ queryKey: ['moderation'] });
    },
  });
  const userId = image.user?.id ?? null;
  const owner = image.user?.firstName || image.user?.username || null;

  return (
    <li className="review-image">
      <div className="review-image__photo">
        <ProfileImage src={image.imageUrl} alt={`Photo ${image.id} awaiting review`} initial="" />
      </div>
      <div className="review-image__body">
        <p>{owner ? `Uploaded by ${owner}` : 'Uploader unknown'}</p>
        {userId !== null && (
          <Link to={MODERATOR_ROUTES.user(userId)} className="btn btn--ghost">
            View profile
          </Link>
        )}
        <Button onClick={() => setConfirming(true)}>Approve</Button>
      </div>
      <ConfirmDialog
        open={confirming}
        title="Approve this photo?"
        confirmLabel="Approve"
        busy={approve.isPending}
        error={approve.isError ? toUserMessage(approve.error, "We couldn't approve the photo.") : null}
        onConfirm={() => approve.mutate()}
        onCancel={() => {
          if (!approve.isPending) {
            setConfirming(false);
            approve.reset();
          }
        }}
      >
        <p>The photo becomes visible on the person&rsquo;s profile.</p>
      </ConfirmDialog>
    </li>
  );
}

export function PendingImagesPage() {
  usePageTitle('Photos awaiting review');
  const pending = useInfiniteQuery({
    queryKey: ['moderation', 'pending-images'],
    queryFn: ({ pageParam, signal }) => listPendingImages({ page: pageParam, size: 20 }, signal),
    initialPageParam: 0,
    getNextPageParam: nextPageParam,
  });
  const items = pending.data?.pages.flatMap((page) => page.content) ?? [];

  return (
    <div className="container page">
      <h1 className="page__title">Photos awaiting review</h1>
      <p className="form-note">
        Photos land here only when the automatic screening service could not decide. The server can approve a photo but has no way to reject one yet.
      </p>
      {pending.isPending ? (
        <LoadingState label="Loading" />
      ) : pending.isError ? (
        <ErrorState error={pending.error} onRetry={() => void pending.refetch()} fallback="We couldn't load the photos." />
      ) : items.length === 0 ? (
        <EmptyState title="Nothing to review" />
      ) : (
        <>
          <ul className="review-images">
            {items.map((image) => (
              <PendingImageRow key={image.id} image={image} />
            ))}
          </ul>
          <LoadMore hasMore={pending.hasNextPage} loading={pending.isFetchingNextPage} onClick={() => void pending.fetchNextPage()} />
        </>
      )}
    </div>
  );
}
