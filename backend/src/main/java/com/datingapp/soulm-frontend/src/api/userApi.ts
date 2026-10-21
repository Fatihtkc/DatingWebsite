import type { ImageDto } from '@/types/social';
import type { UserProfile } from '@/types/user';
import { http } from './axiosInstance';

const UPLOAD_TIMEOUT_MS = 120_000; // up to 25 MB; the default 20 s is too short on a slow connection

/** GET /api/users/profile: the signed-in user's own profile (USER accounts only; staff ids live in other tables). */
export async function getMyProfile(signal?: AbortSignal): Promise<UserProfile> {
  const { data } = await http.get<UserProfile>('/api/users/profile', { signal });
  return data;
}

/**
 * Shape of UploadedImagesResponseDTO. The element types (ImageDTO, BatchUploadResult.RejectedFile)
 * were not supplied, so only the counts are relied upon.
 */
export interface UploadedImages {
  accepted: number;
  rejected: number;
}

/**
 * POST /api/users/me/images. REPLACES every image the user already has with this batch.
 * Limits enforced by the backend: JPG/PNG only, 5 MB each, 10 files, 25 MB total.
 */
export async function uploadMyImages(files: readonly File[], signal?: AbortSignal): Promise<UploadedImages> {
  const body = new FormData();
  for (const file of files) body.append('images', file); // do not set Content-Type: the browser adds the boundary

  const { data } = await http.post<{ images?: unknown; rejected?: unknown }>('/api/users/me/images', body, {
    signal,
    timeout: UPLOAD_TIMEOUT_MS,
  });
  return {
    accepted: Array.isArray(data.images) ? data.images.length : 0,
    rejected: Array.isArray(data.rejected) ? data.rejected.length : 0,
  };
}

/** GET /api/users/{id}. 404 USER_NOT_FOUND when the user does not exist or either side has blocked the other. */
export async function getUserProfile(id: number, signal?: AbortSignal): Promise<UserProfile> {
  const { data } = await http.get<UserProfile>(`/api/users/${id}`, { signal });
  return data;
}

/** GET /api/users/{id}/images: approved images only. */
export async function getUserImages(id: number, signal?: AbortSignal): Promise<ImageDto[]> {
  const { data } = await http.get<ImageDto[]>(`/api/users/${id}/images`, { signal });
  return data;
}
