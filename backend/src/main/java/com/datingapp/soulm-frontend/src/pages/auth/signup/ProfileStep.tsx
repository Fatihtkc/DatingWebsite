import { useState } from 'react';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { Alert } from '@/components/common/Alert';
import { Button } from '@/components/common/Button';
import { FormField } from '@/components/common/FormField';
import { SelectField } from '@/components/common/SelectField';
import { TextAreaField } from '@/components/common/TextAreaField';
import { EMPTY_PROFILE, profileSchema, type ProfileValues } from '@/features/auth/schemas';
import {
  ALCOHOL_OPTIONS,
  BODY_TYPE_OPTIONS,
  DIET_OPTIONS,
  GENDER_OPTIONS,
  RELATIONSHIP_OPTIONS,
  SMOKING_OPTIONS,
} from '@/features/profile/options';
import type { Coordinates, GeolocationControl, LocationStatus } from '@/hooks/useGeolocation';

interface ProfileStepProps {
  defaultValues: ProfileValues | null;
  location: GeolocationControl;
  serverErrors: Record<string, string>;
  /** Failure message from the final signup call, if it was not a field error. */
  submitError: string | null;
  submitting: boolean;
  onBack: () => void;
  onSubmit: (values: ProfileValues, coords: Coordinates) => void;
}

const LOCATION_MESSAGES: Partial<Record<LocationStatus, string>> = {
  denied: "Location access is blocked. Allow it for this site in your browser's settings, then try again.",
  unavailable: "We couldn't work out where you are. Check that location services are on, then try again.",
  timeout: 'Finding your location took too long. Try again.',
  unsupported: "Your browser doesn't support sharing your location, which SoulM needs to show people near you.",
};

function LocationSection({ location, missing }: { location: GeolocationControl; missing: boolean }) {
  const { status } = location;
  const failureMessage = LOCATION_MESSAGES[status];

  return (
    <fieldset className="location-box">
      <legend>Your location</legend>
      <p>
        SoulM uses it to show you people nearby and how far away they are. Other people see a distance, not where you are.
      </p>
      {status === 'granted' ? (
        <p className="location-box__ok" role="status">
          Location saved. <button type="button" className="link-button" onClick={location.request}>Update it</button>
        </p>
      ) : (
        <Button variant="secondary" loading={status === 'requesting'} disabled={status === 'unsupported'} onClick={location.request}>
          {failureMessage && status !== 'unsupported' ? 'Try again' : 'Share my location'}
        </Button>
      )}
      {failureMessage && <p className="field__error" role="alert">{failureMessage}</p>}
      {missing && status !== 'granted' && !failureMessage && (
        <p className="field__error" role="alert">
          Share your location to create your profile.
        </p>
      )}
    </fieldset>
  );
}

export function ProfileStep({ defaultValues, location, serverErrors, submitError, submitting, onBack, onSubmit }: ProfileStepProps) {
  const [locationMissing, setLocationMissing] = useState(false);
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ProfileValues>({ resolver: zodResolver(profileSchema), defaultValues: defaultValues ?? EMPTY_PROFILE });

  const serverLocationError = serverErrors.latitude ?? serverErrors.longitude;
  const err = (field: keyof ProfileValues) => errors[field]?.message ?? serverErrors[field];

  return (
    <form
      className="auth-card"
      noValidate
      onSubmit={handleSubmit(
        (values) => {
          if (!location.coords) {
            setLocationMissing(true);
            return;
          }
          onSubmit(values, location.coords);
        },
        () => {
          if (!location.coords) setLocationMissing(true);
        },
      )}
    >
      {submitError && <Alert tone="error">{submitError}</Alert>}
      <p className="form-note">Only your gender and location are required. Everything else is optional, and you can edit it later.</p>

      <SelectField label="Gender" options={GENDER_OPTIONS} error={err('gender')} {...register('gender')} />
      <LocationSection location={location} missing={locationMissing} />
      {serverLocationError && <p className="field__error" role="alert">{serverLocationError}</p>}

      <FormField label="Town or city" autoComplete="address-level2" hint="Shown on your profile. Optional." error={err('location')} {...register('location')} />

      <div className="form-grid">
        <FormField label="Height (cm)" inputMode="decimal" error={err('height')} {...register('height')} />
        <SelectField label="Body type" options={BODY_TYPE_OPTIONS} error={err('bodyType')} {...register('bodyType')} />
        <SelectField label="Diet" options={DIET_OPTIONS} error={err('diet')} {...register('diet')} />
        <SelectField label="Smoking" options={SMOKING_OPTIONS} error={err('smoke')} {...register('smoke')} />
        <SelectField label="Alcohol" options={ALCOHOL_OPTIONS} error={err('alcohol')} {...register('alcohol')} />
        <SelectField label="Looking for" options={RELATIONSHIP_OPTIONS} error={err('relationshipType')} {...register('relationshipType')} />
      </div>

      <TextAreaField label="About you" hint="Up to 500 characters." maxLength={500} error={err('shorterBio')} {...register('shorterBio')} />
      <TextAreaField label="Hobbies" hint="Up to 1000 characters." maxLength={1000} error={err('hobbies')} {...register('hobbies')} />
      <FormField label="Favourite music" maxLength={200} error={err('favoriteMusic')} {...register('favoriteMusic')} />
      <TextAreaField label="Typical weekend" hint="Up to 500 characters." maxLength={500} error={err('weekendPlans')} {...register('weekendPlans')} />
      <FormField
        label="Age range you are looking for"
        hint="Free text, for example 28 to 35."
        maxLength={500}
        error={err('agePreference')}
        {...register('agePreference')}
      />
      <FormField
        label="How far you would travel"
        hint="Free text, for example up to 30 km."
        maxLength={500}
        error={err('distancePreference')}
        {...register('distancePreference')}
      />

      <div className="auth-page__actions">
        <Button type="submit" loading={submitting}>
          Create my account
        </Button>
        <Button variant="ghost" onClick={onBack}>
          Back
        </Button>
      </div>
    </form>
  );
}
