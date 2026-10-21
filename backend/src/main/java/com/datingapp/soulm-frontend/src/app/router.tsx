import { createBrowserRouter, Navigate } from 'react-router-dom';
import { PublicOnly, RequireAuth } from '@/features/auth/guards';
import { ROLE_HOME } from '@/features/auth/routing';
import { PublicLayout } from '@/layouts/PublicLayout';
import { UserLayout } from '@/layouts/UserLayout';
import { ConversationsPage } from '@/pages/app/ConversationsPage';
import { DiscoverPage } from '@/pages/app/DiscoverPage';
import { LikesPage } from '@/pages/app/LikesPage';
import { MatchesPage } from '@/pages/app/MatchesPage';
import { SignedInPage } from '@/pages/app/SignedInPage';
import { UserProfilePage } from '@/pages/app/UserProfilePage';
import { ForgotPasswordPage } from '@/pages/auth/ForgotPasswordPage';
import { LoginPage } from '@/pages/auth/LoginPage';
import { ResetPasswordPage } from '@/pages/auth/ResetPasswordPage';
import { SignupPage } from '@/pages/auth/SignupPage';
import { LandingPage } from '@/pages/public/LandingPage';
import { NotFoundPage } from '@/pages/public/NotFoundPage';
import { APP_ROUTES, ROUTES } from '@/routes';
import { Role } from '@/types/enums';

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
          { path: '/app/*', element: <NotFoundPage /> },
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
      // Staff areas still use a temporary landing page.
      {
        element: <RequireAuth allow={[Role.MODERATOR]} />,
        children: [{ path: ROLE_HOME[Role.MODERATOR], element: <SignedInPage /> }],
      },
      {
        element: <RequireAuth allow={[Role.MANAGER]} />,
        children: [{ path: ROLE_HOME[Role.MANAGER], element: <SignedInPage /> }],
      },
      { path: '*', element: <NotFoundPage /> },
    ],
  },
]);
