import { Link } from 'react-router-dom';
import { HeroArt } from '@/components/public/HeroArt';
import { usePageTitle } from '@/hooks/usePageTitle';
import { ROUTES } from '@/routes';

const STEPS = [
  {
    title: 'Build your profile',
    body: 'Add photos and a few details about you and what you are looking for. New profiles are reviewed by a moderator before anyone else sees them.',
  },
  {
    title: 'Look at who is nearby',
    body: 'Choose how far you are willing to go, from a few hundred metres up to 100 km, and browse the people around you.',
  },
  {
    title: 'Talk after a match',
    body: 'Like the people you are curious about. When one of them likes you back, it is a match and you can message each other.',
  },
] as const;

const BENEFITS = [
  { term: 'You set the distance', detail: 'Search anywhere from 0.1 km to 100 km around you.' },
  {
    term: 'Messages only between matches',
    detail: 'Nobody can message you unless you have both liked each other.',
  },
  {
    term: 'You choose what you hear about',
    detail: 'Turn notifications for likes, matches and messages on or off separately.',
  },
] as const;

const SAFETY = [
  'Photos are screened automatically. Anything flagged is rejected, and unclear cases go to a moderator.',
  'Moderators review new profiles before they appear in anyone\u2019s results.',
  'Block anyone at any time. They disappear from your results, and any match with them is removed.',
  'Report a profile with a reason and optional screenshots, and a moderator will review it.',
  'Search results show how far away someone is, not where they live.',
] as const;

export function LandingPage() {
  usePageTitle('');

  return (
    <>
      <section className="hero" aria-labelledby="hero-title">
        <div className="container hero__inner">
          <div className="hero__copy">
            <h1 id="hero-title" className="hero__title">
              Find someone nearby. Talk once you both say yes.
            </h1>
            <p className="hero__lead">
              SoulM shows you people within the distance you choose. Like the ones you are curious about; when they like
              you back, you can message each other.
            </p>
            <div className="hero__actions">
              <Link to={ROUTES.signup} className="btn btn--primary btn--large">
                Create your profile
              </Link>
              <Link to={ROUTES.login} className="btn btn--secondary btn--large">
                I already have an account
              </Link>
            </div>
          </div>
          <div className="hero__art">
            <HeroArt />
          </div>
        </div>
      </section>

      <section className="section" aria-labelledby="how-title">
        <div className="container">
          <h2 id="how-title" className="section__title">
            How SoulM works
          </h2>
          <ol className="steps">
            {STEPS.map((step) => (
              <li key={step.title} className="steps__item">
                <h3 className="steps__title">{step.title}</h3>
                <p>{step.body}</p>
              </li>
            ))}
          </ol>
        </div>
      </section>

      <section className="section section--tight" aria-labelledby="benefits-title">
        <div className="container">
          <h2 id="benefits-title" className="section__title">
            What you control
          </h2>
          <dl className="benefits">
            {BENEFITS.map((benefit) => (
              <div key={benefit.term} className="benefits__item">
                <dt>{benefit.term}</dt>
                <dd>{benefit.detail}</dd>
              </div>
            ))}
          </dl>
        </div>
      </section>

      <section className="safety" aria-labelledby="safety-title">
        <div className="container safety__inner">
          <h2 id="safety-title" className="safety__title">
            How we look after you
          </h2>
          <ul className="safety__list">
            {SAFETY.map((item) => (
              <li key={item}>{item}</li>
            ))}
          </ul>
        </div>
      </section>

      <section className="section" aria-labelledby="cta-title">
        <div className="container closing">
          <h2 id="cta-title" className="section__title">
            Your profile takes a few minutes
          </h2>
          <Link to={ROUTES.signup} className="btn btn--primary btn--large">
            Create your profile
          </Link>
        </div>
      </section>
    </>
  );
}
