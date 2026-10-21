import type { ReactNode } from 'react';
import { Link } from 'react-router-dom';
import { ProfileImage } from '@/components/common/ProfileImage';
import { APP_ROUTES } from '@/routes';
import { approvedImages } from '@/features/media/images';
import type { UserProfile } from '@/types/user';
import { ageFromBirthDate, displayName } from '@/utils/format';

interface PersonCardProps {
  profile: UserProfile;
  /** Extra line under the name, such as "Liked on 3 May". */
  detail?: string;
  actions: ReactNode;
}

/** One row of the Likes and Matches lists. */
export function PersonCard({ profile, detail, actions }: PersonCardProps) {
  const name = displayName(profile);
  const age = ageFromBirthDate(profile.birthDate);
  const photo = approvedImages(profile.images)[0];

  return (
    <li className="person">
      <Link to={APP_ROUTES.user(profile.id)} className="person__photo" aria-label={`View ${name}'s profile`}>
        <ProfileImage src={photo?.imageUrl} alt="" initial={name} />
      </Link>
      <div className="person__body">
        <h2 className="person__name">
          <Link to={APP_ROUTES.user(profile.id)}>{age === null ? name : `${name}, ${age}`}</Link>
        </h2>
        {profile.location && <p className="person__meta">{profile.location}</p>}
        {detail && <p className="person__meta">{detail}</p>}
      </div>
      <div className="person__actions">{actions}</div>
    </li>
  );
}
