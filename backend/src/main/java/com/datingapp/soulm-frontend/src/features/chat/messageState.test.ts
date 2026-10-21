import { describe, expect, it } from 'vitest';
import type { ChatMessage } from '@/types/social';
import { expirePending, mergeMessages, settlePending, type PendingMessage } from './messageState';

const msg = (id: number, senderId: number, content: string): ChatMessage => ({
  id, conversationId: 1, senderId, content, sentAt: '2026-01-01T10:00:00', isImage: false, isRead: false,
});
const pending = (tempId: string, content: string, afterId = 0, status: PendingMessage['status'] = 'sending', createdAt = 0): PendingMessage => ({
  tempId, content, afterId, status, createdAt,
});

describe('mergeMessages', () => {
  it('removes duplicates (overlapping history pages, live echoes) and sorts oldest first', () => {
    const merged = mergeMessages([msg(3, 1, 'c'), msg(2, 1, 'b')], [msg(2, 1, 'b'), msg(4, 2, 'd')], [msg(1, 2, 'a')]);
    expect(merged.map((m) => m.id)).toEqual([1, 2, 3, 4]);
  });
});

describe('settlePending', () => {
  it('removes a pending message once the same text from me arrives after it was sent', () => {
    const result = settlePending([pending('1', 'hi', 5)], [msg(6, 7, 'hi')], 7);
    expect(result).toEqual([]);
  });

  it('ignores the other person, older messages and different text', () => {
    const items = [pending('1', 'hi', 5)];
    expect(settlePending(items, [msg(6, 8, 'hi')], 7)).toHaveLength(1); // other sender
    expect(settlePending(items, [msg(5, 7, 'hi')], 7)).toHaveLength(1); // not newer than afterId
    expect(settlePending(items, [msg(6, 7, 'hello')], 7)).toHaveLength(1); // different content
  });

  it('uses one saved message per pending one when the same text was sent twice', () => {
    const items = [pending('1', 'ok', 0), pending('2', 'ok', 0)];
    expect(settlePending(items, [msg(1, 7, 'ok')], 7).map((p) => p.tempId)).toEqual(['2']);
    expect(settlePending(items, [msg(1, 7, 'ok'), msg(2, 7, 'ok')], 7)).toEqual([]);
  });
});

describe('expirePending', () => {
  it('fails messages that waited past the timeout and leaves the rest alone', () => {
    const items = [pending('1', 'a', 0, 'sending', 0), pending('2', 'b', 0, 'sending', 9000), pending('3', 'c', 0, 'failed', 0)];
    const result = expirePending(items, 10_000, 10_000);
    expect(result.map((p) => p.status)).toEqual(['failed', 'sending', 'failed']);
  });

  it('returns the same array when nothing changed, to avoid needless re-renders', () => {
    const items = [pending('1', 'a', 0, 'sending', 9000)];
    expect(expirePending(items, 10_000, 10_000)).toBe(items);
  });
});
