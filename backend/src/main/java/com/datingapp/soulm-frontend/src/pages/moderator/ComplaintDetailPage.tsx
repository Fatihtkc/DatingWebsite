import { useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { ApiError, toUserMessage } from '@/api/errors';
import { Button } from '@/components/common/Button';
import { ConfirmDialog } from '@/components/common/ConfirmDialog';
import { ProfileImage } from '@/components/common/ProfileImage';
import { EmptyState, ErrorState, LoadingState } from '@/components/common/StateViews';
import { ModerationActions } from '@/components/staff/ModerationActions';
import { adminName } from '@/components/staff/UserReviewCard';
import { deleteComplaint, getComplaint } from '@/features/moderation/moderationApi';
import { usePageTitle } from '@/hooks/usePageTitle';
import { MODERATOR_ROUTES } from '@/routes';
import type { Complaint, UserAdmin } from '@/types/account';
import { formatDate } from '@/utils/format';

function Party({ label, user }: { label: string; user: UserAdmin }) {
  return (
    <div className="party">
      <p className="party__label">{label}</p>
      <p>
        <Link to={MODERATOR_ROUTES.user(user.id)} state={{ admin: user }}>
          {adminName(user)}
        </Link>
      </p>
      <p className="person__meta">
        @{user.username}
        {user.email ? ` \u00b7 ${user.email}` : ''}
      </p>
      <p className="badges">
        <span className={user.approved ? 'badge badge--ok' : 'badge badge--wait'}>{user.approved ? 'Approved' : 'Awaiting approval'}</span>
        {user.banned && <span className="badge badge--bad">Banned</span>}
      </p>
    </div>
  );
}

function ComplaintView({ complaint }: { complaint: Complaint }) {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [reported, setReported] = useState<UserAdmin>(complaint.complained);
  const [dismissing, setDismissing] = useState(false);

  const dismiss = useMutation({
    mutationFn: () => deleteComplaint(complaint.id),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['moderation'] });
      navigate(MODERATOR_ROUTES.complaints, { replace: true });
    },
  });

  const evidence = complaint.images ?? [];

  return (
    <>
      <p className="person__meta">Filed on {formatDate(complaint.complaintDate)}</p>
      <div className="parties">
        <Party label="Reported by" user={complaint.complainant} />
        <Party label="Reported person" user={reported} />
      </div>

      <section className="profile__section" aria-labelledby="reason-heading">
        <h2 id="reason-heading" className="profile__heading">
          What happened
        </h2>
        <p className="complaint-reason">{complaint.reason}</p>
      </section>

      <section className="profile__section" aria-labelledby="evidence-heading">
        <h2 id="evidence-heading" className="profile__heading">
          Screenshots
        </h2>
        {evidence.length === 0 ? (
          <p className="form-note">No screenshots were attached.</p>
        ) : (
          <ul className="photo-grid photo-grid--large">
            {evidence.map((image, index) => (
              <li key={image.id} className="photo-grid__item">
                <ProfileImage src={image.imageUrl} alt={`Screenshot ${index + 1} of ${evidence.length}`} initial="" />
              </li>
            ))}
          </ul>
        )}
      </section>

      <section className="profile__section" aria-labelledby="actions-heading">
        <h2 id="actions-heading" className="profile__heading">
          Actions
        </h2>
        <div className="profile__actions">
          <ModerationActions user={reported} name={adminName(reported)} onUpdated={setReported} />
          <Button variant="ghost" onClick={() => setDismissing(true)}>
            Dismiss complaint
          </Button>
        </div>
      </section>

      <ConfirmDialog
        open={dismissing}
        title="Dismiss this complaint?"
        confirmLabel="Dismiss and delete"
        destructive
        busy={dismiss.isPending}
        error={dismiss.isError ? toUserMessage(dismiss.error, "We couldn't dismiss the complaint.") : null}
        onConfirm={() => dismiss.mutate()}
        onCancel={() => {
          if (!dismiss.isPending) {
            setDismissing(false);
            dismiss.reset();
          }
        }}
      >
        <p>
          The server has no &ldquo;resolved&rdquo; state, so dismissing <strong>permanently deletes</strong> the complaint, including its screenshots&rsquo; record. This
          cannot be undone.
        </p>
      </ConfirmDialog>
    </>
  );
}

export function ComplaintDetailPage() {
  const { complaintId } = useParams();
  const id = complaintId !== undefined && /^\d+$/.test(complaintId) ? Number(complaintId) : null;
  usePageTitle('Complaint');

  const complaint = useQuery({
    queryKey: ['moderation', 'complaint', id],
    queryFn: ({ signal }) => getComplaint(id ?? 0, signal),
    enabled: id !== null,
    retry: false,
  });

  let content;
  if (id === null || (complaint.error instanceof ApiError && complaint.error.status === 404)) {
    content = <EmptyState title="Complaint not found">It may already have been dismissed.</EmptyState>;
  } else if (complaint.isPending) {
    content = <LoadingState label="Loading complaint" />;
  } else if (complaint.isError) {
    content = <ErrorState error={complaint.error} onRetry={() => void complaint.refetch()} fallback="We couldn't load this complaint." />;
  } else {
    content = <ComplaintView complaint={complaint.data} />;
  }

  return (
    <div className="container page page--narrow">
      <div className="page__back">
        <Link to={MODERATOR_ROUTES.complaints} className="btn btn--ghost">
          &larr; Complaints
        </Link>
      </div>
      <h1 className="page__title">Complaint</h1>
      {content}
    </div>
  );
}
