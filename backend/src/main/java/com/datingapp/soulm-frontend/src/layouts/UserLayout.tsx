import { Link, NavLink, Outlet, useNavigate } from 'react-router-dom';
import { Button } from '@/components/common/Button';
import { Logo } from '@/components/common/Logo';
import { ChatProvider } from '@/features/chat/ChatProvider';
import { NotificationBell } from '@/features/notifications/NotificationBell';
import { useAuth } from '@/features/auth/useAuth';
import { APP_ROUTES, ROUTES } from '@/routes';
import { cx } from '@/utils/cx';

const NAV = [
  { to: APP_ROUTES.discover, label: 'Discover' },
  { to: APP_ROUTES.likes, label: 'Likes' },
  { to: APP_ROUTES.matches, label: 'Matches' },
  { to: APP_ROUTES.conversations, label: 'Messages' },
  { to: APP_ROUTES.profile, label: 'Profile' },
  { to: APP_ROUTES.settings, label: 'Settings' },
] as const;

/** Chrome for the signed-in member area. */
export function UserLayout() {
  const { signOut } = useAuth();
  const navigate = useNavigate();

  return (
    <ChatProvider>
      <a href="#main" className="skip-link">
        Skip to content
      </a>
      <header className="app-header">
        <div className="container app-header__inner">
          <Link to={APP_ROUTES.home} aria-label="SoulM home" className="site-header__home">
            <Logo />
          </Link>
          <nav className="app-nav" aria-label="Main">
            {NAV.map((item) => (
              <NavLink key={item.to} to={item.to} className={({ isActive }) => cx('app-nav__link', isActive && 'is-active')}>
                {item.label}
              </NavLink>
            ))}
          </nav>
          <NotificationBell />
          <Button
            variant="ghost"
            onClick={() => {
              navigate(ROUTES.home, { replace: true }); // leave the guarded area first, so no redirect to /login
              void signOut();
            }}
          >
            Log out
          </Button>
        </div>
      </header>
      <main id="main" tabIndex={-1} className="app-main">
        <Outlet />
      </main>
    </ChatProvider>
  );
}
