import { z } from 'zod';
import { http } from '@/api/axiosInstance';
import { ApiError } from '@/api/errors';
import type { Page, PageParams } from '@/types/api';
import type { Complaint, PendingImage, StaffProfile, UserAdmin } from '@/types/account';

/** GET /api/moderation/pending-users (MODERATOR). Everyone with approved=false, including banned people. */
export async function listPendingUsers(params: PageParams, signal?: AbortSignal): Promise<Page<UserAdmin>> {
  const { data } = await http.get<Page<UserAdmin>>('/api/moderation/pending-users', { params, signal });
  return data;
}

/** PUT /api/moderation/approve-user/{id} (MODERATOR). Sets approved=true AND banned=false, so it also lifts a ban. */
export async function approveUser(userId: number, signal?: AbortSignal): Promise<UserAdmin> {
  const { data } = await http.put<UserAdmin>(`/api/moderation/approve-user/${userId}`, null, { signal });
  return data;
}

/** PUT /api/moderation/ban-user/{id} (MODERATOR). Revokes the person's refresh tokens; no reason is accepted. */
export async function banUser(userId: number, signal?: AbortSignal): Promise<UserAdmin> {
  const { data } = await http.put<UserAdmin>(`/api/moderation/ban-user/${userId}`, null, { signal });
  return data;
}

/** GET /api/admin/users/search?name= (MODERATOR, MANAGER). An empty name lists everyone. */
export async function searchUsers(name: string, params: PageParams, signal?: AbortSignal): Promise<Page<UserAdmin>> {
  const { data } = await http.get<Page<UserAdmin>>('/api/admin/users/search', { params: { name, ...params }, signal });
  return data;
}

/** GET /api/complaints (MODERATOR, MANAGER), newest first. */
export async function listComplaints(params: PageParams, signal?: AbortSignal): Promise<Page<Complaint>> {
  const { data } = await http.get<Page<Complaint>>('/api/complaints', { params, signal });
  return data;
}

/** GET /api/complaints/{id} */
export async function getComplaint(id: number, signal?: AbortSignal): Promise<Complaint> {
  const { data } = await http.get<Complaint>(`/api/complaints/${id}`, { signal });
  return data;
}

/** DELETE /api/complaints/{id} (MODERATOR). Permanent: there is no "resolved" state. */
export async function deleteComplaint(id: number, signal?: AbortSignal): Promise<void> {
  await http.delete(`/api/complaints/${id}`, { signal });
}

const pendingImageSchema = z.object({
  id: z.number(),
  imageUrl: z.string(),
  user: z
    .object({ id: z.number().nullish(), username: z.string().nullish(), firstName: z.string().nullish() })
    .passthrough()
    .nullish(),
});

/**
 * GET /api/moderation/moderation/pending-images (MODERATOR, MANAGER); the doubled "moderation" is in the
 * controller. Returns UserImage entities, so the rows are validated defensively. Images are PENDING only
 * when the automatic screening service failed, so this queue is normally empty.
 */
export async function listPendingImages(params: PageParams, signal?: AbortSignal): Promise<Page<PendingImage>> {
  const { data } = await http.get<Page<unknown>>('/api/moderation/moderation/pending-images', { params, signal });
  const parsed = z.array(pendingImageSchema).safeParse(data.content);
  if (!parsed.success) throw new ApiError({ kind: 'unknown', message: 'Unexpected response from the server.', cause: parsed.error });
  return { ...data, content: parsed.data.map((row): PendingImage => ({ id: row.id, imageUrl: row.imageUrl, user: row.user ?? null })) };
}

/** PUT /api/moderation/moderation/images/{id}/approve (MODERATOR, MANAGER), 204. There is no reject operation. */
export async function approveImage(imageId: number, signal?: AbortSignal): Promise<void> {
  await http.put(`/api/moderation/moderation/images/${imageId}/approve`, null, { signal });
}

/** GET /api/admin/moderator/{id} (MANAGER, MODERATOR). There is no "me" endpoint: the id comes from the session. */
export async function getModerator(id: number, signal?: AbortSignal): Promise<StaffProfile> {
  const { data } = await http.get<StaffProfile>(`/api/admin/moderator/${id}`, { signal });
  return data;
}
