import type { Role } from '@/types/enums';

/**
 * What the UI knows about the signed-in account. `accountName` is the `username` field of
 * JwtResponseDTO, which is the real username only for USER logins and signups: staff logins
 * and every refresh return the e-mail address instead. Never use it as a display name; load
 * the profile for that.
 */
export interface Session {
  userId: number;
  role: Role;
  accountName: string;
}

export interface AuthResult {
  session: Session;
  accessToken: string;
  refreshToken: string;
}
