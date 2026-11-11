import { http } from '@/api/axiosInstance';
import { Role } from '@/types/enums';

export interface PasswordChange {
  oldPassword: string;
  newPassword: string;
}

/**
 * PUT /api/admin/moderator/change-password or /manager/change-password (PasswordChangeRequestDTO).
 * Each endpoint admits both roles and edits the account of ITS OWN table with the caller's id, so the
 * client must call the one that matches its own role. Neither revokes existing sessions.
 */
export async function changeMyStaffPassword(role: typeof Role.MODERATOR | typeof Role.MANAGER, change: PasswordChange, signal?: AbortSignal): Promise<void> {
  const path = role === Role.MODERATOR ? '/api/admin/moderator/change-password' : '/api/admin/manager/change-password';
  await http.put(path, change, { signal });
}
