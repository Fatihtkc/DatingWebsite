import { z } from 'zod';
import { publicHttp } from '@/api/axiosInstance';
import { ApiError } from '@/api/errors';
import { Role } from '@/types/enums';
import type { UserCreateRequest } from '@/types/user';
import type { AuthResult } from './types';

/** JwtResponseDTO: login, signup and refresh all answer with it. Note the access token is called `token`. */
const jwtResponseSchema = z.object({
  id: z.number(),
  username: z.string(),
  token: z.string().min(1),
  refreshToken: z.string().min(1),
  role: z.nativeEnum(Role),
});

export function parseAuthResponse(data: unknown): AuthResult {
  const parsed = jwtResponseSchema.safeParse(data);
  if (!parsed.success) {
    throw new ApiError({ kind: 'unknown', message: 'Unexpected response from the server.', cause: parsed.error });
  }
  const { id, username, token, refreshToken, role } = parsed.data;
  return { session: { userId: id, role, accountName: username }, accessToken: token, refreshToken };
}

/** POST /api/auth/login. Staff and users share this endpoint; `role` in the answer tells them apart. */
export async function login(email: string, password: string, signal?: AbortSignal): Promise<AuthResult> {
  const { data } = await publicHttp.post<unknown>('/api/auth/login', { email, password }, { signal });
  return parseAuthResponse(data);
}

/** POST /api/auth/refresh. Rotates the refresh token: the old one is dead after this call. */
export async function refresh(refreshToken: string, signal?: AbortSignal): Promise<AuthResult> {
  const { data } = await publicHttp.post<unknown>('/api/auth/refresh', { refreshToken }, { signal });
  return parseAuthResponse(data);
}

/** POST /api/auth/logout (204). Revokes the whole token family. */
export async function logout(refreshToken: string, signal?: AbortSignal): Promise<void> {
  await publicHttp.post('/api/auth/logout', { refreshToken }, { signal });
}

/** GET /api/users/check-user: 200 when both are free, 409 DUPLICATE_RESOURCE naming the first conflict. */
export async function checkUser(email: string, username: string, signal?: AbortSignal): Promise<void> {
  await publicHttp.get('/api/users/check-user', { params: { email, username }, signal });
}

/** POST /api/users/signup. Answers 200 with a session, so the new account is signed in immediately. */
export async function signup(request: UserCreateRequest, signal?: AbortSignal): Promise<AuthResult> {
  const { data } = await publicHttp.post<unknown>('/api/users/signup', request, { signal });
  return parseAuthResponse(data);
}
