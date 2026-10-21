import { Link } from 'react-router-dom';
import { Button } from '@/components/common/Button';
import { ImageGallery } from '@/components/common/ImageGallery';
import { ALCOHOL_OPTIONS, BODY_TYPE_OPTIONS, DIET_OPTIONS, optionLabel, RELATIONSHIP_OPTIONS, SMOKING_OPTIONS } from '@/features/profile/options';
import { APP_ROUTES } from '@/routes';
import type { ImageDto, NearbyUser } from '@/types/social';
import { ageFromBirthDate, displayName, formatDistance, truncate } from '@/utils/format';

interface DiscoverCardProps {
  user: NearbyUser;
  images: readonly ImageDto[];
  busy: boolean;
  onLike: () => void;
  onPass: () => void;
}

const BIO_EXCERPT = 180;

export function DiscoverCard({ user, images, busy, onLike, onPass }: DiscoverCardProps) {
  const name = displayName(user);
  const age = ageFromBirthDate(user.birthDate);
  const distance = formatDistance(user.distance);
  const looking = optionLabel(RELATIONSHIP_OPTIONS, user.relationshipType);
  const facts = [
    user.height ? `${user.height} cm` : null,
    optionLabel(BODY_TYPE_OPTIONS, user.bodyType),
    optionLabel(SMOKING_OPTIONS, user.smoke),
    optionLabel(ALCOHOL_OPTIONS, user.alcohol),
    optionLabel(DIET_OPTIONS, user.diet),
  ].filter((fact): fact is string => fact !== null);

  return (
    <article className="discover-card" aria-labelledby={`discover-${user.id}`}>
      <ImageGallery images={images} name={name} thumbnails={false} />
      <div className="discover-card__body">
        <h2 id={`discover-${user.id}`} className="discover-card__name" tabIndex={-1}>
          {age === null ? name : `${name}, ${age}`}
        </h2>
        <p className="discover-card__meta">{[distance, user.location].filter(Boolean).join(' \u00b7 ')}</p>
        {looking && <p className="discover-card__looking">Looking for: {looking}</p>}
        {user.shorterBio && <p className="discover-card__bio">{truncate(user.shorterBio, BIO_EXCERPT)}</p>}
        {facts.length > 0 && (
          <ul className="chips" aria-label="About">
            {facts.map((fact) => (
              <li key={fact} className="chip">
                {fact}
              </li>
            ))}
          </ul>
        )}
        <div className="discover-card__actions">
          <Button variant="secondary" onClick={onPass} disabled={busy}>
            Pass
          </Button>
          <Link to={APP_ROUTES.user(user.id)} className="btn btn--ghost">
            View profile
          </Link>
          <Button onClick={onLike} loading={busy}>
            Like
          </Button>
        </div>
      </div>
    </article>
  );
}
