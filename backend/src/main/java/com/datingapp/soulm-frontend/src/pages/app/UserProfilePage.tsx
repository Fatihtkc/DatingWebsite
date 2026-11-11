import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom';
import { ApiError, toUserMessage } from '@/api/errors';
import { getUserProfile } from '@/api/userApi';
import { Button } from '@/components/common/Button';
import { ImageGallery } from '@/components/common/ImageGallery';
import { EmptyState, ErrorState, LoadingState } from '@/components/common/StateViews';
import { BlockDialog } from '@/components/dating/BlockDialog';
import { MatchNotice, type LikeNotice } from '@/components/dating/MatchNotice';
import { ProfileDetails } from '@/components/dating/ProfileDetails';
import { ReportDialog } from '@/components/dating/ReportDialog';
import { useAuth } from '@/features/auth/useAuth';
import { useLikeUser } from '@/features/likes/useLikeUser';
import { approvedImages } from '@/features/media/images';
import { usePageTitle } from '@/hooks/usePageTitle';
import { APP_ROUTES } from '@/routes';
import type { UserProfile } from '@/types/user';
import { ageFromBirthDate, displayName } from '@/utils/format';

function ProfileView({ profile, isSelf }: { profile: UserProfile; isSelf: boolean }) {
  const navigate = useNavigate();
  const name = displayName(profile);
  const age = ageFromBirthDate(profile.birthDate);
  const images = approvedImages(profile.images);
  const like = useLikeUser();
  const [notice, setNotice] = useState<LikeNotice | null>(null);
  const [reporting, setReporting] = useState(false);
  const [blocking, setBlocking] = useState(false);

  function onLike() {
    setNotice(null);
    like.mutate(profile.id, {
      onSuccess: (outcome) =>
        setNotice(
          outcome.kind === 'matched'
            ? { kind: 'matched', userId: profile.id, name }
            : outcome.kind === 'already-liked'
              ? { kind: 'already-liked', name }
              : { kind: 'liked', name, matchCheckFailed: outcome.matchCheckFailed },
        ),
      onError: (error) => setNotice({ kind: 'error', text: toUserMessage(error, "We couldn't send your like. Please try again.") }),
    });
  }

  return (
    <div className="profile">
      <ImageGallery images={images} name={name} />
      <div className="profile__main">
        <h1 className="page__title">{age === null ? name : `${name}, ${age}`}</h1>
        {profile.location && <p className="profile__location">{profile.location}</p>}

        {isSelf ? (
          <p className="form-note">
            This is how other people see your profile. <Link to={APP_ROUTES.editProfile}>Edit it</Link>.
          </p>
        ) : (
          <>
            <MatchNotice notice={notice} />
            <div className="profile__actions">
              <Button onClick={onLike} loading={like.isPending}>
                Like
              </Button>
              <Link to={APP_ROUTES.conversation(profile.id)} className="btn btn--secondary">
                Message
              </Link>
              <Button variant="ghost" onClick={() => setReporting(true)}>
                Report
              </Button>
              <Button variant="ghost" onClick={() => setBlocking(true)}>
                Block
              </Button>
            </div>
          </>
        )}

        <ProfileDetails profile={profile} />
      </div>

      <ReportDialog open={reporting} userId={profile.id} name={name} onClose={() => setReporting(false)} />
      <BlockDialog
        open={blocking}
        userId={profile.id}
        name={name}
        onClose={() => setBlocking(false)}
        onBlocked={() => navigate(APP_ROUTES.discover, { replace: true })}
      />
    </div>
  );
}

export function UserProfilePage() {
  const { userId } = useParams();
  const id = userId !== undefined && /^\d+$/.test(userId) ? Number(userId) : null;
  const { state } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  usePageTitle('Profile');

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
      <Link to={APP_ROUTES.discover} className="btn btn--ghost">
        &larr; Discover
      </Link>
    );

  let content;
  if (id === null || (profile.error instanceof ApiError && profile.error.status === 404)) {
    content = <EmptyState title="This profile isn't available">It may have been removed, or you can no longer see each other.</EmptyState>;
  } else if (profile.isPending) {
    content = <LoadingState label="Loading profile" />;
  } else if (profile.isError) {
    content = <ErrorState error={profile.error} onRetry={() => void profile.refetch()} fallback="We couldn't load this profile." />;
  } else {
    content = <ProfileView profile={profile.data} isSelf={state.status === 'authenticated' && state.session.userId === profile.data.id} />;
  }

  return (
    <div className="container page">
      <div className="page__back">{back}</div>
      {content}
    </div>
  );
}
