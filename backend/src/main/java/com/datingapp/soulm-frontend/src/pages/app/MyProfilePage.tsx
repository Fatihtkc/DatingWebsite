import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { getMyProfile } from '@/api/userApi';
import { ImageGallery } from '@/components/common/ImageGallery';
import { ErrorState, LoadingState } from '@/components/common/StateViews';
import { ProfileDetails } from '@/components/dating/ProfileDetails';
import { approvedImages } from '@/features/media/images';
import { usePageTitle } from '@/hooks/usePageTitle';
import { APP_ROUTES } from '@/routes';
import { ageFromBirthDate, displayName } from '@/utils/format';

export function MyProfilePage() {
  usePageTitle('My profile');
  const profile = useQuery({ queryKey: ['my-profile'], queryFn: ({ signal }) => getMyProfile(signal) });

  if (profile.isPending) return <div className="container page"><LoadingState label="Loading your profile" /></div>;
  if (profile.isError) {
    return (
      <div className="container page">
        <ErrorState error={profile.error} onRetry={() => void profile.refetch()} fallback="We couldn't load your profile." />
      </div>
    );
  }

  const me = profile.data;
  const name = displayName(me);
  const age = ageFromBirthDate(me.birthDate);
  const images = approvedImages(me.images);

  return (
    <div className="container page">
      <div className="profile">
        <div className="profile__side">
          <ImageGallery images={images} name={name} />
          {images.length === 0 && (
            <p className="form-note">No photos yet. New photos appear here once they have passed screening.</p>
          )}
        </div>
        <div className="profile__main">
          <h1 className="page__title">{age === null ? name : `${name}, ${age}`}</h1>
          {me.location && <p className="profile__location">{me.location}</p>}
          <div className="profile__actions">
            <Link to={APP_ROUTES.editProfile} className="btn btn--primary">
              Edit profile
            </Link>
            <Link to={APP_ROUTES.user(me.id)} className="btn btn--secondary">
              See how others see it
            </Link>
          </div>
          <ProfileDetails profile={me} />
        </div>
      </div>
    </div>
  );
}
