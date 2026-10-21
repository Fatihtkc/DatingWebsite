import { Link } from 'react-router-dom';
import { usePageTitle } from '@/hooks/usePageTitle';
import { ROUTES } from '@/routes';

export function NotFoundPage() {
  usePageTitle('Page not found');
  return (
    <div className="container auth-page">
      <h1 className="auth-page__title">We can&rsquo;t find that page</h1>
      <p className="auth-page__lead">The link may be broken, or the page may have moved.</p>
      <Link to={ROUTES.home} className="btn btn--primary">
        Go to the home page
      </Link>
    </div>
  );
}
