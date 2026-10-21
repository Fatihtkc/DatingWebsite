import { Link, Navigate, Outlet, useLocation } from 'react-router-dom';
import { PageStatus } from '@/components/common/PageStatus';
import { ROUTES } from '@/routes';
import type { Role } from '@/types/enums';
import { resolvePostLoginTarget, roleHome } from './routing';
import { useAuth } from './useAuth';

/**
 * Client-side gate for a role's area. It only decides what the UI shows: the backend enforces
 * every permission again on each request.
 */
export function RequireAuth({ allow }: { allow: readonly Role[] }) {
  const { state, retry } = useAuth();
  const location = useLocation();

  if (state.status === 'loading') return <PageStatus kind="loading" />;
  if (state.status === 'error') return <PageStatus kind="error" onRetry={retry} />;
  if (state.status === 'anonymous') return <Navigate to={ROUTES.login} replace state={{ from: location }} />;

  if (!allow.includes(state.session.role)) {
    return (
      <div className="container auth-page">
        <h1 className="auth-page__title">You can&rsquo;t open this page</h1>
        <p className="auth-page__lead">Your account doesn&rsquo;t have access to this area.</p>
        <Link to={roleHome(state.session.role)} className="btn btn--primary">
          Go to your home page
        </Link>
      </div>
    );
  }

  return <Outlet />;
}

/** Keeps signed-in people away from login: sends them where they were headed, or to their home. */
export function PublicOnly() {
  const { state } = useAuth();
  const location = useLocation();

  if (state.status === 'loading') return <PageStatus kind="loading" />;
  if (state.status === 'authenticated') {
    return <Navigate to={resolvePostLoginTarget(state.session.role, location.state)} replace />;
  }
  return <Outlet />;
}
