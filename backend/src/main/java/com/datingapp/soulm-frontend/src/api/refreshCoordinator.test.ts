import { describe, expect, it, vi } from 'vitest';
import { refreshAccessToken } from './refreshCoordinator';

describe('refreshAccessToken', () => {
  it('runs one refresh for concurrent callers and shares the result', async () => {
    const perform = vi.fn(async () => 'new-token');
    const results = await Promise.all([refreshAccessToken(perform), refreshAccessToken(perform), refreshAccessToken(perform)]);
    expect(perform).toHaveBeenCalledTimes(1);
    expect(results).toEqual(['new-token', 'new-token', 'new-token']);
  });

  it('allows a fresh refresh once the previous one has settled', async () => {
    const perform = vi.fn(async () => 'token');
    await refreshAccessToken(perform);
    await refreshAccessToken(perform);
    expect(perform).toHaveBeenCalledTimes(2);
  });

  it('passes null through, meaning the session cannot be renewed', async () => {
    await expect(refreshAccessToken(async () => null)).resolves.toBeNull();
  });

  it('rejects every waiting caller on failure and recovers afterwards', async () => {
    const failing = vi.fn(async () => {
      throw new Error('network down');
    });
    const settled = await Promise.allSettled([refreshAccessToken(failing), refreshAccessToken(failing)]);
    expect(failing).toHaveBeenCalledTimes(1);
    expect(settled.map((s) => s.status)).toEqual(['rejected', 'rejected']);
    await expect(refreshAccessToken(async () => 'ok')).resolves.toBe('ok');
  });
});
