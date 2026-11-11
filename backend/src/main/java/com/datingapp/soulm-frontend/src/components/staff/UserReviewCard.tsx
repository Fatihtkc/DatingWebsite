import type { ReactNode } from 'react';
import { Link } from 'react-router-dom';
import { ProfileImage } from '@/components/common/ProfileImage';
import { approvedImages } from '@/features/media/images';
import { MODERATOR_ROUTES } from '@/routes';
import type { UserAdmin } from '@/types/account';
import { fullName } from '@/utils/format';

export function adminName(user: Pick<UserAdmin, 'firstName' | 'lastName' | 'username'>): string {
  return fullName(user) || user.username;
}

/** One person in a moderation list. Admin DTOs carry every image (not only approved ones), which is what a reviewer needs. */
export function UserReviewCard({ user, actions }: { user: UserAdmin; actions: ReactNode }) {
  const name = adminName(user);
  const photo = approvedImages(user.images)[0];

  return (
    <li className="person">
      <span className="person__photo">
        <ProfileImage src={photo?.imageUrl} alt="" initial={name} />
      </span>
      <div className="person__body">
        <h2 className="person__name">
          <Link to={MODERATOR_ROUTES.user(user.id)} state={{ admin: user }}>
            {name}
          </Link>
        </h2>
        <p className="person__meta">
          @{user.username}
          {user.email ? ` \u00b7 ${user.email}` : ''}
        </p>
        <p className="badges">
          <span className={user.approved ? 'badge badge--ok' : 'badge badge--wait'}>{user.approved ? 'Approved' : 'Awaiting approval'}</span>
          {user.banned && <span className="badge badge--bad">Banned</span>}
          {(user.images?.length ?? 0) > 0 && <span className="badge">{user.images?.length} photo(s)</span>}
        </p>
      </div>
      <div className="person__actions">{actions}</div>
    </li>
  );
}
