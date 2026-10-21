import { useId } from 'react';
import { cx } from '@/utils/cx';

interface LogoProps {
  className?: string;
}

/** Two circles meeting: the overlap is the product's idea (a mutual like). */
export function Logo({ className }: LogoProps) {
  const clipId = `lens-${useId().replace(/:/g, '')}`;
  return (
    <span className={cx('logo', className)}>
      <svg className="logo__mark" viewBox="0 0 40 28" width="36" height="25" aria-hidden="true" focusable="false">
        <defs>
          <clipPath id={clipId}>
            <circle cx="15" cy="14" r="12" />
          </clipPath>
        </defs>
        <circle className="art-teal" cx="15" cy="14" r="12" />
        <circle className="art-rose" cx="25" cy="14" r="12" />
        <circle className="art-sun" cx="25" cy="14" r="12" clipPath={`url(#${clipId})`} />
      </svg>
      <span className="logo__word">SoulM</span>
    </span>
  );
}
