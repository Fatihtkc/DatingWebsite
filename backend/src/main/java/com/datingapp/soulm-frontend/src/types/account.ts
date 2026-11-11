import type { NotificationType } from './enums';
import type { ImageDto } from './social';

/**
 * Notification.NotificationDTO. The Java field is `boolean isRead`; Lombok names the accessors
 * isRead()/setRead(), so Jackson's property is `read`.
 * `relatedEntityId` is the id of the LIKE, MATCH or MESSAGE row (not a user or conversation id), so it
 * cannot be used to open a profile or a chat.
 */
export interface NotificationItem {
  id: number;
  type: NotificationType;
  title: string | null;
  body: string | null;
  relatedEntityId: number | null;
  read: boolean;
  createdAt: string;
}

/** User.UserAdminDTO, what moderators and managers see. */
export interface UserAdmin {
  id: number;
  username: string;
  firstName: string | null;
  lastName: string | null;
  email: string | null;
  approved: boolean;
  banned: boolean;
  confirmed: boolean;
  personalityScore: number;
  images: ImageDto[] | null;
}

/** Complaint.ComplaintDTO. There is no status, category or notes: a complaint has a reason and a date. */
export interface Complaint {
  id: number;
  complainant: UserAdmin;
  complained: UserAdmin;
  reason: string;
  complaintDate: string;
  images: ImageDto[] | null;
}

/** Moderator.ModeratorUpdateDTO and Manager.ManagerUpdateDTO share this shape (every field except id may be null). */
export interface StaffProfile {
  id: number;
  firstName: string | null;
  lastName: string | null;
  email: string | null;
  birthDate: string | null;
  startDate: string | null;
  phone: string | null;
  imageUrl: string | null;
}

/**
 * An element of GET /api/moderation/moderation/pending-images. The endpoint returns the UserImage JPA
 * entity itself and the entity was not supplied, so only what any image row must have is required.
 */
export interface PendingImage {
  id: number;
  imageUrl: string;
  user: { id?: number | null; username?: string | null; firstName?: string | null } | null;
}
