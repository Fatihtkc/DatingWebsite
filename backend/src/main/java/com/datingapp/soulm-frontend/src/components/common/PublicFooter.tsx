import { Link } from 'react-router-dom';
import { ROUTES } from '@/routes';
import { Logo } from './Logo';

export function PublicFooter() {
  return (
    <footer className="site-footer">
      <div className="container site-footer__inner">
        <Logo />
        <nav aria-label="Footer">
          <ul className="site-footer__links">
            <li>
              <Link to={ROUTES.login}>Log in</Link>
            </li>
            <li>
              <Link to={ROUTES.signup}>Sign up</Link>
            </li>
            <li>
              <Link to={ROUTES.forgotPassword}>Forgot your password?</Link>
            </li>
          </ul>
        </nav>
        <p className="site-footer__legal">&copy; {new Date().getFullYear()} SoulM</p>
      </div>
    </footer>
  );
}
