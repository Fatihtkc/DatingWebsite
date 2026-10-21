import { normalizeApiError } from '@/api/errors';
import { createMatch } from '@/features/matches/matchesApi';
import type { Match } from '@/types/social';
import { likeUser } from './likesApi';

export type LikeOutcome =
  | { kind: 'matched'; match: Match }
  /** Liked; no match yet. `matchCheckFailed` means we could not ask (rate limit, network) rather than "no". */
  | { kind: 'liked'; matchCheckFailed: boolean }
  | { kind: 'already-liked' };

/**
 * Liking does not create a match on the server; the client has to ask for one afterwards, and the
 * server grants it only when the other person has liked back (409 otherwise). So: like, then ask.
 *
 * A 409 on the like itself ("already liked") deliberately does NOT trigger the match request. After an
 * unmatch both likes still exist, so asking again would silently bring the match back.
 */
export async function likeAndCheckMatch(userId: number): Promise<LikeOutcome> {
  try {
    await likeUser(userId);
  } catch (error) {
    if (normalizeApiError(error).status === 409) return { kind: 'already-liked' };
    throw error;
  }

  try {
    return { kind: 'matched', match: await createMatch(userId) };
  } catch (error) {
    return { kind: 'liked', matchCheckFailed: normalizeApiError(error).status !== 409 };
  }
}
