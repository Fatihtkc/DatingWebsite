import { ModerationStatus } from '@/types/enums';
import type { ImageDto } from '@/types/social';

/** Keeps images that are approved (or carry no status). The backend already filters most responses. */
export function approvedImages(images: readonly ImageDto[] | null | undefined): ImageDto[] {
  return (images ?? []).filter(
    (image) => image.moderationStatus !== ModerationStatus.PENDING && image.moderationStatus !== ModerationStatus.REJECTED,
  );
}
