import { TokenType } from '@/types/enums';
import { publicHttp } from './axiosInstance';

/*
 * VerificationController (/api/auth). All responses are plain-text strings the UI does not display.
 *
 * BACKEND BLOCKER: SecurityConfig only permits login, refresh, logout, signup and check-user without
 * authentication. These five endpoints fall under anyRequest().authenticated(), so anonymous callers
 * receive 401 until they are added to permitAll(). See docs/BACKEND_ANALYSIS.md, finding B1.
 */

/** Sends a 6-digit code, valid 10 minutes. Backend cooldown: 60 s per email, 3 per 10 min, 10 per 24 h. */
export async function sendVerificationCode(email: string, signal?: AbortSignal): Promise<void> {
  // @RequestParam: the email travels in the query string, not a JSON body.
  await publicHttp.post('/api/auth/send-verification', null, { params: { email }, signal });
}

/** Max 5 attempts per code. Wrong, expired and exhausted codes all fail with the same INVALID_OTP error. */
export async function verifyOtp(email: string, otp: string, signal?: AbortSignal): Promise<void> {
  await publicHttp.post('/api/auth/verify', null, { params: { email, otp }, signal });
}

/** Always succeeds for well-formed input, whether or not the account exists (3 per 15 min per email). */
export async function requestPasswordReset(email: string, signal?: AbortSignal): Promise<void> {
  await publicHttp.post('/api/auth/forgot-password', { email }, { signal });
}

/** Resolves if the token is valid; rejects with a 400 if it is invalid, expired or already used. */
export async function validateResetToken(token: string, signal?: AbortSignal): Promise<void> {
  await publicHttp.get('/api/auth/validate', {
    params: { token, type: TokenType.PASSWORD_RESET },
    signal,
  });
}

/** Also revokes every refresh token of the account, so the person is signed out on all devices. */
export async function resetPassword(token: string, newPassword: string, signal?: AbortSignal): Promise<void> {
  await publicHttp.post('/api/auth/reset-password', { token, newPassword }, { signal });
}
