import { http } from '@/api/axiosInstance';
import type { Page, PageParams } from '@/types/api';
import type { ChatMessage } from '@/types/social';

/**
 * POST /api/conversations/{userId}: get or create the conversation with a matched user and return
 * its id. 400 INVALID_REQUEST (generic message) when the two are not matched.
 */
export async function openConversation(otherUserId: number, signal?: AbortSignal): Promise<number> {
  const { data } = await http.post<number>(`/api/conversations/${otherUserId}`, null, { signal });
  return data;
}

/** GET /api/messages/{conversationId}: newest first. Page 0 is the latest page. */
export async function listMessages(conversationId: number, params: PageParams, signal?: AbortSignal): Promise<Page<ChatMessage>> {
  const { data } = await http.get<Page<ChatMessage>>(`/api/messages/${conversationId}`, { params, signal });
  return data;
}
