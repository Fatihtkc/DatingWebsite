import { useState } from 'react';
import { fetchImageAsBlobUrl, resolveMediaUrl } from '@/features/media/mediaUrl';
import { cx } from '@/utils/cx';

interface ProfileImageProps {
  src: string | null | undefined;
  /** Describe the photo, or pass "" when the surrounding text already names the person. */
  alt: string;
  /** Letter shown when there is no usable image. */
  initial?: string;
  className?: string;
}

function Placeholder({ initial, alt, className }: { initial: string; alt: string; className?: string | undefined }) {
  return (
    <div className={cx('avatar', className)} role={alt ? 'img' : undefined} aria-label={alt || undefined} aria-hidden={alt ? undefined : true}>
      <span aria-hidden="true">{initial.charAt(0).toUpperCase() || '?'}</span>
    </div>
  );
}

/**
 * Loads normally first. If the browser cannot (for instance /uploads/** needs the bearer token, which an
 * <img> tag cannot send), retries through an authenticated request, and only then falls back to a placeholder.
 */
function LoadedImage({ url, alt, initial, className }: { url: string; alt: string; initial: string; className?: string | undefined }) {
  const [mode, setMode] = useState<'direct' | 'blob' | 'failed'>('direct');
  const [blobUrl, setBlobUrl] = useState<string | null>(null);

  function onError() {
    if (mode === 'direct') {
      fetchImageAsBlobUrl(url)
        .then((objectUrl) => {
          setBlobUrl(objectUrl);
          setMode('blob');
        })
        .catch(() => setMode('failed'));
    } else {
      setMode('failed');
    }
  }

  if (mode === 'failed' || (mode === 'blob' && !blobUrl)) return <Placeholder initial={initial} alt={alt} className={className} />;
  return <img className={cx('photo', className)} src={mode === 'blob' ? (blobUrl ?? undefined) : url} alt={alt} loading="lazy" decoding="async" onError={onError} />;
}

export function ProfileImage({ src, alt, initial = '', className }: ProfileImageProps) {
  if (!src) return <Placeholder initial={initial} alt={alt} className={className} />;
  const url = resolveMediaUrl(src);
  return <LoadedImage key={url} url={url} alt={alt} initial={initial} className={className} />;
}
