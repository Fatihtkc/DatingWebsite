import { useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { toUserMessage } from '@/api/errors';
import { getMyProfile } from '@/api/userApi';
import { Alert } from '@/components/common/Alert';
import { Button } from '@/components/common/Button';
import { ROLE_LABEL } from '@/features/profile/options';
import { useAuth } from '@/features/auth/useAuth';
import { usePageTitle } from '@/hooks/usePageTitle';
import { ROUTES } from '@/routes';
import { Role } from '@/types/enums';

/**
 * Temporary landing page for /app, /moderator and /manager so login, role redirects, guards, token
 * refresh and logout can be exercised end to end. Replaced by the real area layouts.
 */
export function SignedInPage() {
  usePageTitle('Welcome');
  const { state, signOut } = useAuth();
  const navigate = useNavigate();
  const session = state.status === 'authenticated' ? state.session : null;

  // Also proves that the authenticated Axios instance (bearer token + silent refresh) works.
  const profile = useQuery({
    queryKey: ['my-profile'],
    queryFn: ({ signal }) => getMyProfile(signal),
    enabled: session?.role === Role.USER,
  });

  if (!session) return null;

  // Staff logins and refreshes return the e-mail address as `username`, and users get one after a refresh too.
  const name = profile.data?.firstName ?? (session.role === Role.USER ? null : session.accountName);

  return (
    <div className="container auth-page">
      <h1 className="auth-page__title">{name ? `Welcome, ${name}` : 'Welcome'}</h1>
      <p className="auth-page__lead">You are signed in as a {ROLE_LABEL[session.role]}.</p>
      {profile.isError && <Alert tone="error">{toUserMessage(profile.error, "We couldn't load your profile.")}</Alert>}
      <p className="auth-page__footnote">This page is temporary. The rest of your area is being built.</p>
      <Button
        variant="secondary"
        onClick={() => {
          navigate(ROUTES.home, { replace: true });
          void signOut();
        }}
      >
        Log out
      </Button>
    </div>
  );
}
