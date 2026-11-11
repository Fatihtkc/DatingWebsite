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
  profile: '/app/profile',
  notifications: '/app/notifications',
  settings: '/app/settings',
  editProfile: '/app/settings/profile',
  blockedUsers: '/app/settings/blocked',
} as const;

/** Moderator area. */
export const MODERATOR_ROUTES = {
  home: '/moderator',
  pendingUsers: '/moderator/pending-users',
  pendingImages: '/moderator/pending-images',
  complaints: '/moderator/complaints',
  complaint: (id: number) => `/moderator/complaints/${id}`,
  users: '/moderator/users',
  user: (id: number) => `/moderator/users/${id}`,
  profile: '/moderator/profile',
  password: '/moderator/settings/password',
} as const;
