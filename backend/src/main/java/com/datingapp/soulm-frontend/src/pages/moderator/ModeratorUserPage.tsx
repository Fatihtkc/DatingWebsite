import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom';
import { ApiError } from '@/api/errors';
import { getUserProfile } from '@/api/userApi';
import { Alert } from '@/components/common/Alert';
import { Button } from '@/components/common/Button';
import { ImageGallery } from '@/components/common/ImageGallery';
import { EmptyState, ErrorState, LoadingState } from '@/components/common/StateViews';
import { ProfileDetails } from '@/components/dating/ProfileDetails';
import { ModerationActions } from '@/components/staff/ModerationActions';
import { approvedImages } from '@/features/media/images';
import { usePageTitle } from '@/hooks/usePageTitle';
import { MODERATOR_ROUTES } from '@/routes';
import type { UserAdmin } from '@/types/account';
import { ageFromBirthDate, displayName, fullName } from '@/utils/format';

/** The list pages pass the admin DTO along in router state; anything else is ignored. */
function readAdminState(state: unknown): UserAdmin | null {
  if (typeof state !== 'object' || state === null || !('admin' in state)) return null;
  const admin = (state as { admin: unknown }).admin;
  if (typeof admin !== 'object' || admin === null) return null;
  const candidate = admin as Partial<UserAdmin>;
  return typeof candidate.id === 'number' && typeof candidate.approved === 'boolean' && typeof candidate.banned === 'boolean' ? (candidate as UserAdmin) : null;
}

export function ModeratorUserPage() {
  const { userId } = useParams();
  const id = userId !== undefined && /^\d+$/.test(userId) ? Number(userId) : null;
  const location = useLocation();
  const navigate = useNavigate();
  usePageTitle('Review person');

  // Approval and ban flags exist only on the admin DTO, and no endpoint returns one by id,
  // so they are known only when this page was opened from a list (or after an action here).
  const [admin, setAdmin] = useState<UserAdmin | null>(() => {
    const fromList = readAdminState(location.state);
    return fromList && fromList.id === id ? fromList : null;
  });

  const profile = useQuery({
    queryKey: ['user', id],
    queryFn: ({ signal }) => getUserProfile(id ?? 0, signal),
    enabled: id !== null,
    retry: false,
  });

  const back =
    location.key !== 'default' ? (
      <Button variant="ghost" onClick={() => navigate(-1)}>
        &larr; Back
      </Button>
    ) : (
      <Link to={MODERATOR_ROUTES.home} className="btn btn--ghost">
        &larr; Overview
      </Link>
    );

  let content;
  if (id === null || (profile.error instanceof ApiError && profile.error.status === 404)) {
    content = <EmptyState title="This profile isn't available" />;
  } else if (profile.isPending) {
    content = <LoadingState label="Loading profile" />;
  } else if (profile.isError) {
    content = <ErrorState error={profile.error} onRetry={() => void profile.refetch()} fallback="We couldn't load this profile." />;
  } else {
    const person = profile.data;
    const name = displayName(person);
    const age = ageFromBirthDate(person.birthDate);
    // The admin DTO lists every image (including ones still awaiting review); the public profile lists approved ones.
    const images = admin?.images && admin.images.length > 0 ? admin.images : approvedImages(person.images);
    const label = fullName(person) || person.username;

    content = (
      <div className="profile">
        <ImageGallery images={images} name={name} />
        <div className="profile__main">
          <h1 className="page__title">{age === null ? label : `${label}, ${age}`}</h1>
          <p className="profile__location">@{person.username}{admin?.email ? ` \u00b7 ${admin.email}` : ''}</p>

          {admin ? (
            <p className="badges">
              <span className={admin.approved ? 'badge badge--ok' : 'badge badge--wait'}>{admin.approved ? 'Approved' : 'Awaiting approval'}</span>
              {admin.banned && <span className="badge badge--bad">Banned</span>}
            </p>
          ) : (
            <Alert tone="info">Current approval and ban status isn&rsquo;t available here. Open this person from a list to see it. Both actions are offered.</Alert>
          )}
          <div className="profile__actions">
            <ModerationActions user={admin ?? { id: person.id, approved: false, banned: false }} name={label} onUpdated={setAdmin} />
          </div>

          <ProfileDetails profile={person} />
        </div>
      </div>
    );
  }

  return (
    <div className="container page">
      <div className="page__back">{back}</div>
      {content}
    </div>
  );
}
