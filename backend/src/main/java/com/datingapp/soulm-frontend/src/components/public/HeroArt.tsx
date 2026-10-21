import { useId } from 'react';

/** The brand idea at full size: two people, one overlap. Decorative, so hidden from assistive tech. */
export function HeroArt() {
  const clipId = `hero-lens-${useId().replace(/:/g, '')}`;
  return (
    <svg className="hero-art" viewBox="0 0 520 380" aria-hidden="true" focusable="false">
      <defs>
        <clipPath id={clipId}>
          <circle cx="190" cy="190" r="150" />
        </clipPath>
      </defs>
      <circle className="hero-art__a art-teal" cx="190" cy="190" r="150" />
      <g className="hero-art__b">
        <circle className="art-rose" cx="330" cy="190" r="150" />
        <circle className="art-sun" cx="330" cy="190" r="150" clipPath={`url(#${clipId})`} />
      </g>
      {/* speech bubble in the overlap */}
      <g className="hero-art__bubble">
        <rect x="230" y="160" width="60" height="40" rx="12" />
        <path d="M246 199 L240 214 L258 199 Z" />
      </g>
    </svg>
  );
}
