import { AxiosError, AxiosHeaders, type AxiosResponse, type InternalAxiosRequestConfig } from 'axios';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ApiError } from '@/api/errors';
import type { Match, UserLike } from '@/types/social';
import { createMatch } from '@/features/matches/matchesApi';
import { likeAndCheckMatch } from './likeFlow';
import { likeUser } from './likesApi';

vi.mock('./likesApi', () => ({ likeUser: vi.fn() }));
vi.mock('@/features/matches/matchesApi', () => ({ createMatch: vi.fn() }));

const apiError = (status: number) => new ApiError({ kind: 'http', status, message: 'x' });
const match = { id: 9 } as Match;

beforeEach(() => {
  vi.mocked(likeUser).mockReset();
  vi.mocked(createMatch).mockReset();
});

describe('likeAndCheckMatch', () => {
  it('reports a match when the match request succeeds', async () => {
    vi.mocked(likeUser).mockResolvedValue({ id: 1 } as UserLike);
    vi.mocked(createMatch).mockResolvedValue(match);
    await expect(likeAndCheckMatch(5)).resolves.toEqual({ kind: 'matched', match });
    expect(createMatch).toHaveBeenCalledWith(5);
  });

  it('reports "liked" when the other person has not liked back (409)', async () => {
    vi.mocked(likeUser).mockResolvedValue({ id: 1 } as UserLike);
    vi.mocked(createMatch).mockRejectedValue(apiError(409));
    await expect(likeAndCheckMatch(5)).resolves.toEqual({ kind: 'liked', matchCheckFailed: false });
  });

  it('keeps the like and says the check failed on rate limits or network errors', async () => {
    vi.mocked(likeUser).mockResolvedValue({ id: 1 } as UserLike);
    vi.mocked(createMatch).mockRejectedValue(apiError(429));
    await expect(likeAndCheckMatch(5)).resolves.toEqual({ kind: 'liked', matchCheckFailed: true });
  });

  it('does NOT ask for a match when the person was already liked, so an unmatch cannot be undone by accident', async () => {
    vi.mocked(likeUser).mockRejectedValue(apiError(409));
    await expect(likeAndCheckMatch(5)).resolves.toEqual({ kind: 'already-liked' });
    expect(createMatch).not.toHaveBeenCalled();
  });

  it('passes other like failures (such as 403 for blocked users) to the caller', async () => {
    vi.mocked(likeUser).mockRejectedValue(apiError(403));
    await expect(likeAndCheckMatch(5)).rejects.toMatchObject({ status: 403 });
    expect(createMatch).not.toHaveBeenCalled();
  });

  it('also copes with raw Axios errors', async () => {
    const config = { headers: new AxiosHeaders() } as InternalAxiosRequestConfig;
    const response = { status: 409, statusText: '', data: {}, headers: {}, config } as AxiosResponse;
    vi.mocked(likeUser).mockRejectedValue(new AxiosError('conflict', 'ERR_BAD_REQUEST', config, null, response));
    await expect(likeAndCheckMatch(5)).resolves.toEqual({ kind: 'already-liked' });
  });
});
