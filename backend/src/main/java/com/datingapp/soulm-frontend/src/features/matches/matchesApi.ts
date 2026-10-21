import { http } from '@/api/axiosInstance';
import type { Page, PageParams } from '@/types/api';
import type { Match } from '@/types/social';

/** GET /api/matches */
export async function listMatches(params: PageParams, signal?: AbortSignal): Promise<Page<Match>> {
  const { data } = await http.get<Page<Match>>('/api/matches', { params, signal });
  return data;
}

/**
 * POST /api/matches?user2Id=. Matches are not created by the like itself: the client asks for one.
 * The server creates it only if BOTH likes exist (idempotent), otherwise 409 CONFLICT. Answers 200.
 */
export async function createMatch(otherUserId: number, signal?: AbortSignal): Promise<Match> {
  const { data } = await http.post<Match>('/api/matches', null, { params: { user2Id: otherUserId }, signal });
  return data;
}

/** DELETE /api/matches/{matchId}. 204; 403 when the caller is not part of the match. */
export async function deleteMatch(matchId: number, signal?: AbortSignal): Promise<void> {
  await http.delete(`/api/matches/${matchId}`, { signal });
}
