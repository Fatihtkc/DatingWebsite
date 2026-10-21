import { useState } from 'react';
import type { ImageDto } from '@/types/social';
import { ProfileImage } from './ProfileImage';

interface ImageGalleryProps {
  images: readonly ImageDto[];
  /** Name used in alt text and for the placeholder letter. */
  name: string;
  thumbnails?: boolean;
}

export function ImageGallery({ images, name, thumbnails = true }: ImageGalleryProps) {
  const [index, setIndex] = useState(0);
  const safeIndex = Math.min(index, Math.max(images.length - 1, 0));
  const current = images[safeIndex];

  return (
    <div className="gallery">
      <div className="gallery__main">
        <ProfileImage
          key={current?.id ?? 'none'}
          src={current?.imageUrl}
          alt={images.length > 0 ? `Photo ${safeIndex + 1} of ${images.length} of ${name}` : `${name} has no photos yet`}
          initial={name}
          className="gallery__image"
        />
        {images.length > 1 && (
          <>
            <button
              type="button"
              className="gallery__step gallery__step--prev"
              aria-label="Previous photo"
              onClick={() => setIndex((safeIndex - 1 + images.length) % images.length)}
            >
              &lsaquo;
            </button>
            <button
              type="button"
              className="gallery__step gallery__step--next"
              aria-label="Next photo"
              onClick={() => setIndex((safeIndex + 1) % images.length)}
            >
              &rsaquo;
            </button>
          </>
        )}
      </div>
      {thumbnails && images.length > 1 && (
        <ul className="gallery__thumbs">
          {images.map((image, position) => (
            <li key={image.id}>
              <button
                type="button"
                className="gallery__thumb"
                aria-label={`Show photo ${position + 1}`}
                aria-current={position === safeIndex ? 'true' : undefined}
                onClick={() => setIndex(position)}
              >
                <ProfileImage src={image.imageUrl} alt="" initial={name} />
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
