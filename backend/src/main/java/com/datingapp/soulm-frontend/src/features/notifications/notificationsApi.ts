import { http } from '@/api/axiosInstance';
import type { Page, PageParams } from '@/types/api';
import type { NotificationItem } from '@/types/account';

/** GET /api/notifications, newest first. */
export async function listNotifications(params: PageParams, signal?: AbortSignal): Promise<Page<NotificationItem>> {
  const { data } = await http.get<Page<NotificationItem>>('/api/notifications', { params, signal });
  return data;
}

/** GET /api/notifications/unread-count: a bare JSON number. */
export async function getUnreadCount(signal?: AbortSignal): Promise<number> {
  const { data } = await http.get<number>('/api/notifications/unread-count', { signal });
  return data;
}

/** PUT /api/notifications/mark-all-read. There is no way to mark a single notification. */
export async function markAllNotificationsRead(signal?: AbortSignal): Promise<void> {
  await http.put('/api/notifications/mark-all-read', null, { signal });
}
