import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { ProfileImage } from '@/components/common/ProfileImage';
import { ErrorState, LoadingState } from '@/components/common/StateViews';
import { useAuth } from '@/features/auth/useAuth';
import { usePageTitle } from '@/hooks/usePageTitle';
import type { StaffProfile } from '@/types/account';
import { formatDate, fullName } from '@/utils/format';

interface StaffProfilePageProps {
  /** Loads the signed-in staff member's own record (there is no "me" endpoint, so it is fetched by session id). */
  load: (id: number, signal?: AbortSignal) => Promise<StaffProfile>;
  queryKey: string;
  passwordTo: string;
  /** Sentence about who can change these details. */
  editNote: string;
}

export function StaffProfilePage({ load, queryKey, passwordTo, editNote }: StaffProfilePageProps) {
  usePageTitle('My profile');
  const { state } = useAuth();
  const myId = state.status === 'authenticated' ? state.session.userId : null;
  const profile = useQuery({
    queryKey: ['staff', queryKey, myId],
    queryFn: ({ signal }) => load(myId ?? 0, signal),
    enabled: myId !== null,
  });

  if (profile.isPending) return <div className="container page"><LoadingState label="Loading your profile" /></div>;
  if (profile.isError) {
    return (
      <div className="container page">
        <ErrorState error={profile.error} onRetry={() => void profile.refetch()} fallback="We couldn't load your profile." />
      </div>
    );
  }

  const me = profile.data;
  const name = fullName(me) || me.email || 'Your profile';
  const rows: ReadonlyArray<readonly [string, string]> = [
    ['Email', me.email ?? ''],
    ['Phone', me.phone ?? ''],
    ['Date of birth', formatDate(me.birthDate)],
    ['Started', formatDate(me.startDate)],
  ];

  return (
    <div className="container page page--narrow">
      <h1 className="page__title">My profile</h1>
      <div className="staff-card">
        <div className="staff-card__photo">
          <ProfileImage src={me.imageUrl} alt="" initial={name} />
        </div>
        <div>
          <h2 className="profile__heading">{name}</h2>
          <dl className="facts">
            {rows
              .filter(([, value]) => value !== '')
              .map(([label, value]) => (
                <div key={label} className="facts__row">
                  <dt>{label}</dt>
                  <dd>{value}</dd>
                </div>
              ))}
          </dl>
        </div>
      </div>
      <p className="form-note">{editNote}</p>
      <div>
        <Link to={passwordTo} className="btn btn--secondary">
          Change password
        </Link>
      </div>
    </div>
  );
}
