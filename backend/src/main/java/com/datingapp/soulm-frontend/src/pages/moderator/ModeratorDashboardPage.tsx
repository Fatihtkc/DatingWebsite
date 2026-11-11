import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { toUserMessage } from '@/api/errors';
import { listComplaints, listPendingImages, listPendingUsers } from '@/features/moderation/moderationApi';
import { usePageTitle } from '@/hooks/usePageTitle';
import { MODERATOR_ROUTES } from '@/routes';

function QueueCard({ title, to, count, error }: { title: string; to: string; count: number | undefined; error: unknown }) {
  return (
    <li className="queue-card">
      <h2 className="queue-card__title">{title}</h2>
      <p className="queue-card__count" aria-live="polite">
        {error ? '\u2013' : count === undefined ? '\u2026' : count}
      </p>
      {error ? <p className="field__error">{toUserMessage(error, "Couldn't load.")}</p> : null}
      <Link to={to} className="btn btn--secondary">
        Open
      </Link>
    </li>
  );
}

/** Counts come from one-item pages: the backend has no statistics endpoint for these queues. */
export function ModeratorDashboardPage() {
  usePageTitle('Moderation');
  const users = useQuery({ queryKey: ['moderation', 'count', 'users'], queryFn: async ({ signal }) => (await listPendingUsers({ page: 0, size: 1 }, signal)).totalElements });
  const images = useQuery({ queryKey: ['moderation', 'count', 'images'], queryFn: async ({ signal }) => (await listPendingImages({ page: 0, size: 1 }, signal)).totalElements });
  const complaints = useQuery({ queryKey: ['moderation', 'count', 'complaints'], queryFn: async ({ signal }) => (await listComplaints({ page: 0, size: 1 }, signal)).totalElements });

  return (
    <div className="container page">
      <h1 className="page__title">Moderation</h1>
      <p className="form-note">What is waiting for review right now.</p>
      <ul className="queue-cards">
        <QueueCard title="People awaiting approval" to={MODERATOR_ROUTES.pendingUsers} count={users.data} error={users.error} />
        <QueueCard title="Photos awaiting review" to={MODERATOR_ROUTES.pendingImages} count={images.data} error={images.error} />
        <QueueCard title="Complaints" to={MODERATOR_ROUTES.complaints} count={complaints.data} error={complaints.error} />
      </ul>
    </div>
  );
}
