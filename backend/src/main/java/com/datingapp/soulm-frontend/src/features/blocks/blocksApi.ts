import { http } from '@/api/axiosInstance';
import type { Page, PageParams } from '@/types/api';
import type { UserProfile } from '@/types/user';

/**
 * POST /api/blocks {blockedUserId} (200, empty). Also deletes any match between the two. Afterwards the
 * blocker can no longer open the blocked person's profile (404), and neither can like or message the other.
 */
export async function blockUser(blockedUserId: number, signal?: AbortSignal): Promise<void> {
  await http.post('/api/blocks', { blockedUserId }, { signal });
}

/** DELETE /api/blocks/{blockedUserId} (204). */
export async function unblockUser(blockedUserId: number, signal?: AbortSignal): Promise<void> {
  await http.delete(`/api/blocks/${blockedUserId}`, { signal });
}

/** GET /api/blocks: the people the caller has blocked. */
export async function listBlockedUsers(params: PageParams, signal?: AbortSignal): Promise<Page<UserProfile>> {
  const { data } = await http.get<Page<UserProfile>>('/api/blocks', { params, signal });
  return data;
}
