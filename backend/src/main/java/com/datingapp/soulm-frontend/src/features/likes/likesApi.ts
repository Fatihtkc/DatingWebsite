import { http } from '@/api/axiosInstance';
import type { Page, PageParams } from '@/types/api';
import type { UserLike } from '@/types/social';

/**
 * GET /api/likes/{userId}: the people `userId` has liked. The path carries the caller's own id.
 * BACKEND BUG: LikeController#listLikes has no @PathVariable for `userId`, so its
 * @PreAuthorize("#userId == principal.id ...") never matches and every call answers 403.
 */
export async function listMyLikes(userId: number, params: PageParams, signal?: AbortSignal): Promise<Page<UserLike>> {
  const { data } = await http.get<Page<UserLike>>(`/api/likes/${userId}`, { params, signal });
  return data;
}

/** POST /api/likes/users/{likedId}/like. 201; 409 CONFLICT when already liked; 403 when blocked. */
export async function likeUser(likedId: number, signal?: AbortSignal): Promise<UserLike> {
  const { data } = await http.post<UserLike>(`/api/likes/users/${likedId}/like`, null, { signal });
  return data;
}

/** DELETE /api/likes/{likeId} (the like's id, not the user's). 204. */
export async function removeLike(likeId: number, signal?: AbortSignal): Promise<void> {
  await http.delete(`/api/likes/${likeId}`, { signal });
}
