import { Link } from 'react-router-dom';
import { ROUTES } from '@/routes';
import { Alert } from './Alert';
import { Button } from './Button';

type PageStatusProps = { kind: 'loading' } | { kind: 'error'; onRetry: () => void };

/** Full-width placeholder shown while the session is being restored. */
export function PageStatus(props: PageStatusProps) {
  if (props.kind === 'loading') {
    return (
      <div className="container page-status" role="status" aria-live="polite">
        <span className="btn__spinner page-status__spinner" aria-hidden="true" />
        <p>Loading&hellip;</p>
      </div>
    );
  }
  return (
    <div className="container auth-page">
      <h1 className="auth-page__title">We couldn&rsquo;t restore your session</h1>
      <Alert tone="error">We couldn&rsquo;t reach SoulM to check that you are still signed in.</Alert>
      <div className="auth-page__actions">
        <Button onClick={props.onRetry}>Try again</Button>
        <Link to={ROUTES.login} className="btn btn--ghost">
          Go to log in
        </Link>
      </div>
    </div>
  );
}
