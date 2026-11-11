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
  /** False for people whose profile cannot be opened (blocked). */
  linkToProfile?: boolean;
}

/** One row of the Likes and Matches lists. */
export function PersonCard({ profile, detail, actions, linkToProfile = true }: PersonCardProps) {
  const name = displayName(profile);
  const age = ageFromBirthDate(profile.birthDate);
  const photo = approvedImages(profile.images)[0];
  const title = age === null ? name : `${name}, ${age}`;

  return (
    <li className="person">
      {linkToProfile ? (
        <Link to={APP_ROUTES.user(profile.id)} className="person__photo" aria-label={`View ${name}'s profile`}>
          <ProfileImage src={photo?.imageUrl} alt="" initial={name} />
        </Link>
      ) : (
        <span className="person__photo">
          <ProfileImage src={photo?.imageUrl} alt="" initial={name} />
        </span>
      )}
      <div className="person__body">
        <h2 className="person__name">
          {linkToProfile ? <Link to={APP_ROUTES.user(profile.id)}>{title}</Link> : title}
        </h2>
        {profile.location && <p className="person__meta">{profile.location}</p>}
        {detail && <p className="person__meta">{detail}</p>}
      </div>
      <div className="person__actions">{actions}</div>
    </li>
  );
}
