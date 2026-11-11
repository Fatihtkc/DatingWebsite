import { Link } from 'react-router-dom';
import { usePageTitle } from '@/hooks/usePageTitle';
import { APP_ROUTES } from '@/routes';

export function SettingsPage() {
  usePageTitle('Settings');
  return (
    <div className="container page page--narrow">
      <h1 className="page__title">Settings</h1>
      <ul className="settings-links">
        <li>
          <Link to={APP_ROUTES.editProfile} className="settings-link">
            <span className="settings-link__title">Edit profile</span>
            <span>Your details, location and photos.</span>
          </Link>
        </li>
        <li>
          <Link to={APP_ROUTES.blockedUsers} className="settings-link">
            <span className="settings-link__title">Blocked people</span>
            <span>See who you have blocked, and unblock them.</span>
          </Link>
        </li>
      </ul>
      <p className="form-note">
        Changing your password or email, notification preferences and deleting your account are not available yet: the server doesn&rsquo;t offer them for
        members. If you forgot your password, you can <Link to="/forgot-password">reset it by email</Link>.
      </p>
    </div>
  );
}
