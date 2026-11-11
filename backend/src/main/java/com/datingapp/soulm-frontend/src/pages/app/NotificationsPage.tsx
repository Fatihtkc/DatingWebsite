import { useInfiniteQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { toUserMessage } from '@/api/errors';
import { Alert } from '@/components/common/Alert';
import { Button } from '@/components/common/Button';
import { EmptyState, ErrorState, LoadingState, LoadMore } from '@/components/common/StateViews';
import { listNotifications, markAllNotificationsRead } from '@/features/notifications/notificationsApi';
import { notificationTarget } from '@/features/notifications/notificationTarget';
import { usePageTitle } from '@/hooks/usePageTitle';
import { cx } from '@/utils/cx';
import { formatMessageTime, nextPageParam } from '@/utils/format';

export function NotificationsPage() {
  usePageTitle('Notifications');
  const queryClient = useQueryClient();
  const notifications = useInfiniteQuery({
    queryKey: ['notifications', 'list'],
    queryFn: ({ pageParam, signal }) => listNotifications({ page: pageParam, size: 20 }, signal),
    initialPageParam: 0,
    getNextPageParam: nextPageParam,
  });
  const markAll = useMutation({
    mutationFn: () => markAllNotificationsRead(),
    onSuccess: () => void queryClient.invalidateQueries({ queryKey: ['notifications'] }),
  });

  const items = notifications.data?.pages.flatMap((page) => page.content) ?? [];
  const hasUnread = items.some((item) => !item.read);

  return (
    <div className="container page page--narrow">
      <div className="page__header">
        <h1 className="page__title">Notifications</h1>
        <Button variant="secondary" disabled={!hasUnread} loading={markAll.isPending} onClick={() => markAll.mutate()}>
          Mark all as read
        </Button>
      </div>
      {markAll.isError && <Alert tone="error">{toUserMessage(markAll.error, "We couldn't mark them as read.")}</Alert>}

      {notifications.isPending ? (
        <LoadingState label="Loading notifications" />
      ) : notifications.isError ? (
        <ErrorState error={notifications.error} onRetry={() => void notifications.refetch()} fallback="We couldn't load your notifications." />
      ) : items.length === 0 ? (
        <EmptyState title="Nothing new">Likes, matches and messages show up here.</EmptyState>
      ) : (
        <>
          <ul className="notifications">
            {items.map((item) => {
              const target = notificationTarget(item.type);
              return (
                <li key={item.id} className={cx('notification', !item.read && 'notification--unread')}>
                  <div>
                    <p className="notification__title">
                      {!item.read && <span className="visually-hidden">Unread: </span>}
                      {item.title ?? 'Notification'}
                    </p>
                    {item.body && <p className="notification__body">{item.body}</p>}
                    <p className="notification__meta">{formatMessageTime(item.createdAt)}</p>
                  </div>
                  <Link to={target.to} className="btn btn--ghost">{target.label}</Link>
                </li>
              );
            })}
          </ul>
          <LoadMore hasMore={notifications.hasNextPage} loading={notifications.isFetchingNextPage} onClick={() => void notifications.fetchNextPage()} />
        </>
      )}
    </div>
  );
}
