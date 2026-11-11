import { APP_ROUTES } from '@/routes';
import { NotificationType } from '@/types/enums';

/**
 * Where a notification leads. `relatedEntityId` is a like, match or message id, which no page can
 * open directly, so each type leads to the list that contains the thing it is about. A like is
 * anonymous ("Someone likes you!"), so it leads to Discover.
 */
export function notificationTarget(type: NotificationType): { to: string; label: string } {
  switch (type) {
    case NotificationType.NEW_MATCH:
      return { to: APP_ROUTES.matches, label: 'View matches' };
    case NotificationType.NEW_MESSAGE:
      return { to: APP_ROUTES.conversations, label: 'Open messages' };
    case NotificationType.NEW_LIKE:
      return { to: APP_ROUTES.discover, label: 'Go to Discover' };
  }
}
