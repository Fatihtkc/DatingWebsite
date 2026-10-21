import { Link } from 'react-router-dom';
import { roleHome } from '@/features/auth/routing';
import { useAuth } from '@/features/auth/useAuth';
import { ROUTES } from '@/routes';
import { Logo } from './Logo';

export function PublicHeader() {
  const { state } = useAuth();
  return (
    <header className="site-header">
      <div className="container site-header__inner">
        <Link to={ROUTES.home} className="site-header__home" aria-label="SoulM home">
          <Logo />
        </Link>
        <nav className="site-header__nav" aria-label="Account">
          {state.status === 'authenticated' && (
            <Link to={roleHome(state.session.role)} className="btn btn--primary">
              Open SoulM
            </Link>
          )}
          {(state.status === 'anonymous' || state.status === 'error') && (
            <>
              <Link to={ROUTES.login} className="btn btn--ghost">
                Log in
              </Link>
              <Link to={ROUTES.signup} className="btn btn--primary">
                Sign up
              </Link>
            </>
          )}
        </nav>
      </div>
    </header>
  );
}
