import { describe, expect, it } from 'vitest';
import { notificationTarget } from './notificationTarget';

describe('notificationTarget', () => {
  it('leads each type to the list that holds what it is about', () => {
    expect(notificationTarget('NEW_MATCH').to).toBe('/app/matches');
    expect(notificationTarget('NEW_MESSAGE').to).toBe('/app/conversations');
    expect(notificationTarget('NEW_LIKE').to).toBe('/app/discover');
  });
});
