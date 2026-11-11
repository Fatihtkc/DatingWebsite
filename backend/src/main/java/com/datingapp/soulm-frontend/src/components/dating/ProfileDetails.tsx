import {
  ALCOHOL_OPTIONS,
  BODY_TYPE_OPTIONS,
  DIET_OPTIONS,
  GENDER_OPTIONS,
  optionLabel,
  RELATIONSHIP_OPTIONS,
  SMOKING_OPTIONS,
} from '@/features/profile/options';
import type { UserProfile } from '@/types/user';

type Fact = readonly [label: string, value: string | null];

function FactList({ title, facts }: { title: string; facts: readonly Fact[] }) {
  const present = facts.filter((fact): fact is readonly [string, string] => fact[1] !== null && fact[1] !== '');
  if (present.length === 0) return null;
  return (
    <section className="profile__section" aria-labelledby={`section-${title}`}>
      <h2 id={`section-${title}`} className="profile__heading">
        {title}
      </h2>
      <dl className="facts">
        {present.map(([label, value]) => (
          <div key={label} className="facts__row">
            <dt>{label}</dt>
            <dd>{value}</dd>
          </div>
        ))}
      </dl>
    </section>
  );
}

/** The descriptive part of a profile, shared by other people's profiles, your own, and the moderator view. */
export function ProfileDetails({ profile }: { profile: UserProfile }) {
  return (
    <>
      {profile.shorterBio && (
        <section className="profile__section" aria-labelledby="section-about">
          <h2 id="section-about" className="profile__heading">
            About
          </h2>
          <p>{profile.shorterBio}</p>
        </section>
      )}
      <FactList
        title="Basics"
        facts={[
          ['Gender', optionLabel(GENDER_OPTIONS, profile.gender)],
          ['Height', profile.height ? `${profile.height} cm` : null],
          ['Body type', optionLabel(BODY_TYPE_OPTIONS, profile.bodyType)],
          ['Looking for', optionLabel(RELATIONSHIP_OPTIONS, profile.relationshipType)],
        ]}
      />
      <FactList
        title="Lifestyle"
        facts={[
          ['Smoking', optionLabel(SMOKING_OPTIONS, profile.smoke)],
          ['Alcohol', optionLabel(ALCOHOL_OPTIONS, profile.alcohol)],
          ['Diet', optionLabel(DIET_OPTIONS, profile.diet)],
        ]}
      />
      <FactList
        title="Interests"
        facts={[
          ['Hobbies', profile.hobbies],
          ['Favourite music', profile.favoriteMusic],
          ['Typical weekend', profile.weekendPlans],
        ]}
      />
    </>
  );
}
