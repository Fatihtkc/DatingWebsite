export const ROUTES = {
  home: '/',
  login: '/login',
  signup: '/signup',
  forgotPassword: '/forgot-password',
  resetPassword: '/reset-password',
} as const;

/** Signed-in member area. */
export const APP_ROUTES = {
  home: '/app',
  discover: '/app/discover',
  likes: '/app/likes',
  matches: '/app/matches',
  conversations: '/app/conversations',
  user: (id: number) => `/app/users/${id}`,
  conversation: (otherUserId: number) => `/app/conversations/${otherUserId}`,
} as const;
