import { useEffect, useMemo, useState } from 'react';
import { zodResolver } from '@hookform/resolvers/zod';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { Link } from 'react-router-dom';
import { ApiError, toUserMessage } from '@/api/errors';
import { getMyProfile, uploadMyImages } from '@/api/userApi';
import { Alert } from '@/components/common/Alert';
import { Button } from '@/components/common/Button';
import { ConfirmDialog } from '@/components/common/ConfirmDialog';
import { FormField } from '@/components/common/FormField';
import { PhotoPicker } from '@/components/common/PhotoPicker';
import { ProfileImage } from '@/components/common/ProfileImage';
import { SelectField } from '@/components/common/SelectField';
import { ErrorState, LoadingState } from '@/components/common/StateViews';
import { TextAreaField } from '@/components/common/TextAreaField';
import { approvedImages } from '@/features/media/images';
import { editProfileSchema, type EditProfileValues } from '@/features/profile/editSchema';
import {
  ALCOHOL_OPTIONS,
  BODY_TYPE_OPTIONS,
  DIET_OPTIONS,
  GENDER_OPTIONS,
  RELATIONSHIP_OPTIONS,
  SMOKING_OPTIONS,
} from '@/features/profile/options';
import { checkPhotos, MAX_PHOTOS } from '@/features/profile/photoRules';
import { updateMyProfile } from '@/features/profile/profileApi';
import { buildUpdateRequest, toEditDefaults } from '@/features/profile/updatePayload';
import { useGeolocation } from '@/hooks/useGeolocation';
import { usePageTitle } from '@/hooks/usePageTitle';
import { APP_ROUTES } from '@/routes';
import type { UserProfile } from '@/types/user';
import { formatDate } from '@/utils/format';

function PhotoSection({ profile }: { profile: UserProfile }) {
  const queryClient = useQueryClient();
  const current = approvedImages(profile.images);
  const [files, setFiles] = useState<File[]>([]);
  const [confirming, setConfirming] = useState(false);
  const problems = useMemo(() => checkPhotos(files), [files]);

  const replace = useMutation({
    mutationFn: () => uploadMyImages(files),
    onSuccess: () => {
      setConfirming(false);
      setFiles([]);
      void queryClient.invalidateQueries({ queryKey: ['my-profile'] });
      void queryClient.invalidateQueries({ queryKey: ['user', profile.id] });
    },
  });

  return (
    <section className="edit-section" aria-labelledby="photos-heading">
      <h2 id="photos-heading" className="profile__heading">
        Photos
      </h2>
      {current.length > 0 ? (
        <ul className="photo-grid">
          {current.map((image, index) => (
            <li key={image.id} className="photo-grid__item">
              <ProfileImage src={image.imageUrl} alt={`Your photo ${index + 1} of ${current.length}`} initial="" />
            </li>
          ))}
        </ul>
      ) : (
        <p className="form-note">You have no visible photos yet.</p>
      )}
      <p className="form-note">
        Uploading photos <strong>replaces all of your current photos</strong>: photos cannot be added or removed one at a time. Choose up to {MAX_PHOTOS}{' '}
        (JPG or PNG, 5 MB each). New photos are screened before they appear.
      </p>
      {replace.isSuccess && (
        <Alert tone="success">
          {replace.data.accepted > 0 ? 'Your photos were replaced.' : 'No photos were added.'}
          {replace.data.rejected > 0 && ` ${replace.data.rejected} could not be used (content rules or file type).`}
        </Alert>
      )}
      <PhotoPicker files={files} problems={problems} onChange={(next) => { replace.reset(); setFiles(next); }} label="Choose new photos" />
      <div>
        <Button variant="secondary" disabled={files.length === 0 || problems.length > 0} onClick={() => setConfirming(true)}>
          Replace my photos
        </Button>
      </div>
      <ConfirmDialog
        open={confirming}
        title="Replace all your photos?"
        confirmLabel="Replace photos"
        destructive
        busy={replace.isPending}
        error={replace.isError ? toUserMessage(replace.error, "We couldn't upload your photos. Your current photos are unchanged.") : null}
        onConfirm={() => replace.mutate()}
        onCancel={() => { if (!replace.isPending) { setConfirming(false); replace.reset(); } }}
      >
        <p>Your current photos will be removed and replaced with the {files.length} you chose.</p>
      </ConfirmDialog>
    </section>
  );
}

function EditProfileForm({ profile }: { profile: UserProfile }) {
  const queryClient = useQueryClient();
  const location = useGeolocation();
  const { request: requestLocation } = location;
  const [locationMissing, setLocationMissing] = useState(false);
  const [serverErrors, setServerErrors] = useState<Record<string, string>>({});

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<EditProfileValues>({ resolver: zodResolver(editProfileSchema), defaultValues: toEditDefaults(profile) });

  // The update endpoint insists on coordinates, so ask for them up front when the browser already allows it.
  useEffect(() => {
    navigator.permissions
      ?.query({ name: 'geolocation' })
      .then((status) => {
        if (status.state === 'granted') requestLocation();
      })
      .catch(() => undefined);
  }, [requestLocation]);

  const save = useMutation({
    mutationFn: ({ values, coords }: { values: EditProfileValues; coords: NonNullable<typeof location.coords> }) =>
      updateMyProfile(profile.id, buildUpdateRequest(profile, values, coords)),
    onSuccess: () => {
      setServerErrors({});
      void queryClient.invalidateQueries({ queryKey: ['my-profile'] });
      void queryClient.invalidateQueries({ queryKey: ['user', profile.id] });
    },
    onError: (error) => {
      if (error instanceof ApiError && error.status === 400) setServerErrors({ ...error.fieldErrors });
    },
  });

  const err = (field: keyof EditProfileValues) => errors[field]?.message ?? serverErrors[field];
  const coords = location.coords;
  const showGeneralError = save.isError && Object.keys(serverErrors).length === 0;

  return (
    <form
      className="auth-card"
      noValidate
      onSubmit={handleSubmit(
        (values) => {
          if (!coords) return setLocationMissing(true);
          setLocationMissing(false);
          save.mutate({ values, coords });
        },
        () => { if (!coords) setLocationMissing(true); },
      )}
    >
      {save.isSuccess && <Alert tone="success">Your profile was saved.</Alert>}
      {showGeneralError && <Alert tone="error">{toUserMessage(save.error, "We couldn't save your profile. Please try again.")}</Alert>}

      <div className="form-grid">
        <FormField label="First name" autoComplete="given-name" error={err('firstName')} {...register('firstName')} />
        <FormField label="Last name" autoComplete="family-name" error={err('lastName')} {...register('lastName')} />
      </div>
      <p className="form-note">
        Date of birth: {formatDate(profile.birthDate) || 'not set'}. It can&rsquo;t be changed here.
      </p>
      <SelectField label="Gender" options={GENDER_OPTIONS} error={err('gender')} {...register('gender')} />

      <fieldset className="location-box">
        <legend>Your location</legend>
        <p>
          Saving your profile also updates the location SoulM uses to find people near you. Other people see a distance, not where you are.
        </p>
        {coords ? (
          <p className="location-box__ok" role="status">Location ready.</p>
        ) : (
          <Button variant="secondary" loading={location.status === 'requesting'} disabled={location.status === 'unsupported'} onClick={requestLocation}>
            Use my current location
          </Button>
        )}
        {location.status === 'denied' && <p className="field__error" role="alert">Location access is blocked. Allow it for this site in your browser&rsquo;s settings, then try again.</p>}
        {(location.status === 'unavailable' || location.status === 'timeout') && <p className="field__error" role="alert">We couldn&rsquo;t work out where you are. Try again.</p>}
        {location.status === 'unsupported' && <p className="field__error" role="alert">Your browser doesn&rsquo;t support sharing your location, which saving requires.</p>}
        {locationMissing && !coords && location.status === 'idle' && <p className="field__error" role="alert">Share your location to save your profile.</p>}
        {(serverErrors.latitude ?? serverErrors.longitude) && <p className="field__error" role="alert">{serverErrors.latitude ?? serverErrors.longitude}</p>}
      </fieldset>

      <FormField label="Town or city" autoComplete="address-level2" error={err('location')} {...register('location')} />
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

      <Alert tone="info">
        The two fields below can&rsquo;t be loaded from the server, so they start empty. <strong>Saving replaces what is stored with what you enter here, and
        leaving them blank clears them.</strong>
      </Alert>
      <FormField label="Age range you are looking for" hint="Free text, for example 28 to 35." maxLength={500} error={err('agePreference')} {...register('agePreference')} />
      <FormField label="How far you would travel" hint="Free text, for example up to 30 km." maxLength={500} error={err('distancePreference')} {...register('distancePreference')} />

      <div className="auth-page__actions">
        <Button type="submit" loading={save.isPending}>Save changes</Button>
        <Link to={APP_ROUTES.profile} className="btn btn--ghost">Back to my profile</Link>
      </div>
    </form>
  );
}

export function EditProfilePage() {
  usePageTitle('Edit profile');
  const profile = useQuery({ queryKey: ['my-profile'], queryFn: ({ signal }) => getMyProfile(signal) });

  return (
    <div className="container page page--narrow">
      <h1 className="page__title">Edit profile</h1>
      {profile.isPending ? (
        <LoadingState label="Loading your profile" />
      ) : profile.isError ? (
        <ErrorState error={profile.error} onRetry={() => void profile.refetch()} fallback="We couldn't load your profile." />
      ) : (
        <>
          <EditProfileForm profile={profile.data} />
          <PhotoSection profile={profile.data} />
        </>
      )}
    </div>
  );
}
