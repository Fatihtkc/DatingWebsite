import { http } from '@/api/axiosInstance';
import type { UserProfile, UserUpdateRequest } from '@/types/user';

/** PUT /api/users/profile/{id}. Only the owner (role USER) may call it. Overwrites every field: see UserUpdateRequest. */
export async function updateMyProfile(id: number, request: UserUpdateRequest, signal?: AbortSignal): Promise<UserProfile> {
  const { data } = await http.put<UserProfile>(`/api/users/profile/${id}`, request, { signal });
  return data;
}
