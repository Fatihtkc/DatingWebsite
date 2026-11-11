import { createBrowserRouter, Navigate } from 'react-router-dom';
import { PublicOnly, RequireAuth } from '@/features/auth/guards';
import { ROLE_HOME } from '@/features/auth/routing';
import { PublicLayout } from '@/layouts/PublicLayout';
import { StaffLayout } from '@/layouts/StaffLayout';
import { UserLayout } from '@/layouts/UserLayout';
import { getModerator } from '@/features/moderation/moderationApi';
import { BlockedUsersPage } from '@/pages/app/BlockedUsersPage';
import { ConversationsPage } from '@/pages/app/ConversationsPage';
import { DiscoverPage } from '@/pages/app/DiscoverPage';
import { EditProfilePage } from '@/pages/app/EditProfilePage';
import { LikesPage } from '@/pages/app/LikesPage';
import { MatchesPage } from '@/pages/app/MatchesPage';
import { MyProfilePage } from '@/pages/app/MyProfilePage';
import { NotificationsPage } from '@/pages/app/NotificationsPage';
import { SettingsPage } from '@/pages/app/SettingsPage';
import { SignedInPage } from '@/pages/app/SignedInPage';
import { UserProfilePage } from '@/pages/app/UserProfilePage';
import { ForgotPasswordPage } from '@/pages/auth/ForgotPasswordPage';
import { LoginPage } from '@/pages/auth/LoginPage';
import { ResetPasswordPage } from '@/pages/auth/ResetPasswordPage';
import { SignupPage } from '@/pages/auth/SignupPage';
import { ComplaintDetailPage } from '@/pages/moderator/ComplaintDetailPage';
import { ComplaintsPage } from '@/pages/moderator/ComplaintsPage';
import { ModeratorDashboardPage } from '@/pages/moderator/ModeratorDashboardPage';
import { ModeratorUserPage } from '@/pages/moderator/ModeratorUserPage';
import { PendingImagesPage } from '@/pages/moderator/PendingImagesPage';
import { PendingUsersPage } from '@/pages/moderator/PendingUsersPage';
import { StaffPasswordPage } from '@/pages/moderator/StaffPasswordPage';
import { StaffProfilePage } from '@/pages/moderator/StaffProfilePage';
import { UserSearchPage } from '@/pages/moderator/UserSearchPage';
import { LandingPage } from '@/pages/public/LandingPage';
import { NotFoundPage } from '@/pages/public/NotFoundPage';
import { APP_ROUTES, MODERATOR_ROUTES, ROUTES } from '@/routes';
import { Role } from '@/types/enums';

const MODERATOR_NAV = [
  { to: MODERATOR_ROUTES.home, label: 'Overview', end: true },
  { to: MODERATOR_ROUTES.pendingUsers, label: 'Awaiting approval' },
  { to: MODERATOR_ROUTES.pendingImages, label: 'Photos' },
  { to: MODERATOR_ROUTES.complaints, label: 'Complaints' },
  { to: MODERATOR_ROUTES.users, label: 'People' },
  { to: MODERATOR_ROUTES.profile, label: 'Profile' },
] as const;

export const router = createBrowserRouter([
  {
    // Member area: its own layout (and chat connection owner).
    element: <RequireAuth allow={[Role.USER]} />,
    children: [
      {
        element: <UserLayout />,
        children: [
          { path: APP_ROUTES.home, element: <Navigate to={APP_ROUTES.discover} replace /> },
          { path: APP_ROUTES.discover, element: <DiscoverPage /> },
          { path: '/app/users/:userId', element: <UserProfilePage /> },
          { path: APP_ROUTES.likes, element: <LikesPage /> },
          { path: APP_ROUTES.matches, element: <MatchesPage /> },
          { path: APP_ROUTES.conversations, element: <ConversationsPage /> },
          { path: '/app/conversations/:userId', element: <ConversationsPage /> },
          { path: APP_ROUTES.profile, element: <MyProfilePage /> },
          { path: APP_ROUTES.settings, element: <SettingsPage /> },
          { path: APP_ROUTES.editProfile, element: <EditProfilePage /> },
          { path: APP_ROUTES.blockedUsers, element: <BlockedUsersPage /> },
          { path: APP_ROUTES.notifications, element: <NotificationsPage /> },
          { path: '/app/*', element: <NotFoundPage /> },
        ],
      },
    ],
  },
  {
    element: <RequireAuth allow={[Role.MODERATOR]} />,
    children: [
      {
        element: <StaffLayout label="Moderator" homeTo={MODERATOR_ROUTES.home} items={MODERATOR_NAV} />,
        children: [
          { path: MODERATOR_ROUTES.home, element: <ModeratorDashboardPage /> },
          { path: MODERATOR_ROUTES.pendingUsers, element: <PendingUsersPage /> },
          { path: MODERATOR_ROUTES.pendingImages, element: <PendingImagesPage /> },
          { path: MODERATOR_ROUTES.complaints, element: <ComplaintsPage /> },
          { path: '/moderator/complaints/:complaintId', element: <ComplaintDetailPage /> },
          { path: MODERATOR_ROUTES.users, element: <UserSearchPage /> },
          { path: '/moderator/users/:userId', element: <ModeratorUserPage /> },
          {
            path: MODERATOR_ROUTES.profile,
            element: (
              <StaffProfilePage
                load={getModerator}
                queryKey="moderator"
                passwordTo={MODERATOR_ROUTES.password}
                editNote="Your details are managed by a manager: moderators can't edit their own profile."
              />
            ),
          },
          { path: MODERATOR_ROUTES.password, element: <StaffPasswordPage backTo={MODERATOR_ROUTES.profile} /> },
          { path: '/moderator/*', element: <NotFoundPage /> },
        ],
      },
    ],
  },
  {
    element: <PublicLayout />,
    children: [
      { path: ROUTES.home, element: <LandingPage /> },
      { element: <PublicOnly />, children: [{ path: ROUTES.login, element: <LoginPage /> }] },
      // Not behind PublicOnly: the signup flow stays open after the account exists, for the photos step.
      { path: ROUTES.signup, element: <SignupPage /> },
      { path: ROUTES.forgotPassword, element: <ForgotPasswordPage /> },
      { path: ROUTES.resetPassword, element: <ResetPasswordPage /> },
      // The manager area still uses a temporary landing page.
      {
        element: <RequireAuth allow={[Role.MANAGER]} />,
        children: [{ path: ROLE_HOME[Role.MANAGER], element: <SignedInPage /> }],
      },
      { path: '*', element: <NotFoundPage /> },
    ],
  },
]);
