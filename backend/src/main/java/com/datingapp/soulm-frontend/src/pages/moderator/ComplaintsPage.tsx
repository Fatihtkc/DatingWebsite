import { useInfiniteQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { EmptyState, ErrorState, LoadingState, LoadMore } from '@/components/common/StateViews';
import { adminName } from '@/components/staff/UserReviewCard';
import { listComplaints } from '@/features/moderation/moderationApi';
import { usePageTitle } from '@/hooks/usePageTitle';
import { MODERATOR_ROUTES } from '@/routes';
import { formatDate, nextPageParam, truncate } from '@/utils/format';

export function ComplaintsPage() {
  usePageTitle('Complaints');
  const complaints = useInfiniteQuery({
    queryKey: ['moderation', 'complaints'],
    queryFn: ({ pageParam, signal }) => listComplaints({ page: pageParam, size: 20 }, signal),
    initialPageParam: 0,
    getNextPageParam: nextPageParam,
  });
  const items = complaints.data?.pages.flatMap((page) => page.content) ?? [];

  return (
    <div className="container page">
      <h1 className="page__title">Complaints</h1>
      <p className="form-note">Newest first. The server stores a reason and a date only: no status, category or search.</p>
      {complaints.isPending ? (
        <LoadingState label="Loading" />
      ) : complaints.isError ? (
        <ErrorState error={complaints.error} onRetry={() => void complaints.refetch()} fallback="We couldn't load the complaints." />
      ) : items.length === 0 ? (
        <EmptyState title="No complaints" />
      ) : (
        <>
          <ul className="complaints">
            {items.map((complaint) => (
              <li key={complaint.id} className="complaint-row">
                <div>
                  <p className="complaint-row__parties">
                    <strong>{adminName(complaint.complainant)}</strong> reported <strong>{adminName(complaint.complained)}</strong>
                  </p>
                  <p className="complaint-row__reason">{truncate(complaint.reason, 160)}</p>
                  <p className="person__meta">{formatDate(complaint.complaintDate)}</p>
                </div>
                <Link to={MODERATOR_ROUTES.complaint(complaint.id)} className="btn btn--secondary">
                  Open
                </Link>
              </li>
            ))}
          </ul>
          <LoadMore hasMore={complaints.hasNextPage} loading={complaints.isFetchingNextPage} onClick={() => void complaints.fetchNextPage()} />
        </>
      )}
    </div>
  );
}
