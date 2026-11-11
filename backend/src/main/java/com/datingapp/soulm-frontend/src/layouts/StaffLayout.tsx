import { Link, NavLink, Outlet, useNavigate } from 'react-router-dom';
import { Button } from '@/components/common/Button';
import { Logo } from '@/components/common/Logo';
import { useAuth } from '@/features/auth/useAuth';
import { ROUTES } from '@/routes';
import { cx } from '@/utils/cx';

export interface StaffNavItem {
  to: string;
  label: string;
  /** Match the exact path only (for the overview page). */
  end?: boolean;
}

interface StaffLayoutProps {
  /** Shown next to the logo, for example "Moderator". */
  label: string;
  homeTo: string;
  items: readonly StaffNavItem[];
}

/** Chrome shared by the moderator and manager areas. */
export function StaffLayout({ label, homeTo, items }: StaffLayoutProps) {
  const { signOut } = useAuth();
  const navigate = useNavigate();

  return (
    <>
      <a href="#main" className="skip-link">
        Skip to content
      </a>
      <header className="app-header app-header--staff">
        <div className="container app-header__inner">
          <Link to={homeTo} aria-label={`SoulM ${label} home`} className="site-header__home staff-brand">
            <Logo />
            <span className="staff-brand__label">{label}</span>
          </Link>
          <nav className="app-nav" aria-label="Main">
            {items.map((item) => (
              <NavLink key={item.to} to={item.to} end={item.end} className={({ isActive }) => cx('app-nav__link', isActive && 'is-active')}>
                {item.label}
              </NavLink>
            ))}
          </nav>
          <Button
            variant="ghost"
            onClick={() => {
              navigate(ROUTES.home, { replace: true });
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
    </>
  );
}
