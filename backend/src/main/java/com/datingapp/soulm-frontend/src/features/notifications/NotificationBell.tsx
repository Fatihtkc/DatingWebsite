import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { APP_ROUTES } from '@/routes';
import { getUnreadCount } from './notificationsApi';

const POLL_MS = 60_000;

/**
 * There is no real-time channel for notifications (the WebSocket only carries chat), so the count is
 * polled once a minute while the tab is visible.
 */
export function NotificationBell() {
  const unread = useQuery({
    queryKey: ['notifications', 'unread'],
    queryFn: ({ signal }) => getUnreadCount(signal),
    refetchInterval: POLL_MS,
  });
  const count = unread.data ?? 0;

  return (
    <Link to={APP_ROUTES.notifications} className="bell" aria-label={count > 0 ? `Notifications, ${count} unread` : 'Notifications'}>
      <svg viewBox="0 0 24 24" width="22" height="22" aria-hidden="true" focusable="false" className="bell__icon">
        <path d="M12 3a6 6 0 0 0-6 6v3.5L4 16v1h16v-1l-2-3.5V9a6 6 0 0 0-6-6Zm-2 16a2 2 0 0 0 4 0Z" fill="currentColor" />
      </svg>
      {count > 0 && (
        <span className="bell__badge" aria-hidden="true">
          {count > 99 ? '99+' : count}
        </span>
      )}
    </Link>
  );
}
