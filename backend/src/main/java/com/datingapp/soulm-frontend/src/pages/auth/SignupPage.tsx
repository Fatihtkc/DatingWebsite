import { useState } from 'react';
import { useMutation } from '@tanstack/react-query';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import { ApiError, normalizeApiError, toUserMessage } from '@/api/errors';
import { sendVerificationCode } from '@/api/verificationApi';
import { PageStatus } from '@/components/common/PageStatus';
import { Stepper } from '@/components/common/Stepper';
import * as authApi from '@/features/auth/authApi';
import { roleHome } from '@/features/auth/routing';
import type { AccountValues, ProfileValues } from '@/features/auth/schemas';
import { buildSignupRequest } from '@/features/auth/signupPayload';
import { useAuth } from '@/features/auth/useAuth';
import { useGeolocation, type Coordinates } from '@/hooks/useGeolocation';
import { usePageTitle } from '@/hooks/usePageTitle';
import { ROUTES } from '@/routes';
import { Role } from '@/types/enums';
import { AccountStep } from './signup/AccountStep';
import { PhotosStep } from './signup/PhotosStep';
import { ProfileStep } from './signup/ProfileStep';
import { VerifyStep } from './signup/VerifyStep';

type StepId = 'account' | 'verify' | 'profile' | 'photos';

const STEPS: ReadonlyArray<{ id: StepId; label: string; title: string }> = [
  { id: 'account', label: 'Your account', title: 'Create your account' },
  { id: 'verify', label: 'Verify email', title: 'Verify your email' },
  { id: 'profile', label: 'About you', title: 'Tell us about you' },
  { id: 'photos', label: 'Photos', title: 'Add your photos' },
];

/**
 * The backend remembers a verified address for 15 minutes (EmailVerification.expiresAt) and the
 * account is created in one request at the end of the profile step. Leave a minute of margin.
 */
const VERIFICATION_VALID_MS = 14 * 60_000;

const ACCOUNT_FIELDS = new Set(['username', 'firstName', 'lastName', 'email', 'password', 'birthDate']);

function stepForFields(fields: string[]): StepId {
  return fields.some((field) => ACCOUNT_FIELDS.has(field)) ? 'account' : 'profile';
}

export function SignupPage() {
  usePageTitle('Create your profile');
  const { state, signIn } = useAuth();
  const navigate = useNavigate();
  const location = useGeolocation();

  const [step, setStep] = useState<StepId>('account');
  const [account, setAccount] = useState<AccountValues | null>(null);
  const [profile, setProfile] = useState<ProfileValues | null>(null);
  const [verified, setVerified] = useState<{ email: string; at: number } | null>(null);
  const [codeSentAt, setCodeSentAt] = useState<number | null>(null);
  const [notice, setNotice] = useState<string | null>(null);
  const [serverErrors, setServerErrors] = useState<Record<string, string>>({});

  // Check that the username and email are free, then send the code. One action, so one error to show.
  const start = useMutation({
    mutationFn: async (values: AccountValues) => {
      await authApi.checkUser(values.email, values.username);
      await sendVerificationCode(values.email);
    },
    onSuccess: () => {
      setCodeSentAt(Date.now());
      setNotice(null);
      setStep('verify');
    },
  });

  const create = useMutation({
    mutationFn: (variables: { account: AccountValues; profile: ProfileValues; coords: Coordinates }) =>
      authApi.signup(buildSignupRequest(variables.account, variables.profile, variables.coords)),
    onSuccess: (result) => {
      setStep('photos'); // before signIn, so no render is "signed in but still on an earlier step" (that redirects away)
      signIn(result); // the new account is signed in immediately; photos are uploaded with its token
    },
    onError: (error) => {
      const apiError = normalizeApiError(error);
      const fields = Object.keys(apiError.fieldErrors);
      if (apiError.status === 400 && fields.length > 0) {
        setServerErrors({ ...apiError.fieldErrors });
        setStep(stepForFields(fields));
      } else if (apiError.status === 409) {
        // Someone took the email or username between the availability check and now.
        setServerErrors({ [/^username/i.test(apiError.message) ? 'username' : 'email']: apiError.message });
        setStep('account');
      }
    },
  });

  // Signed-in people have no business here, except right after signing up (photos step).
  if (state.status === 'loading') return <PageStatus kind="loading" />;
  if (state.status === 'authenticated' && step !== 'photos') return <Navigate to={roleHome(state.session.role)} replace />;

  const currentIndex = STEPS.findIndex((item) => item.id === step);
  const current = STEPS[currentIndex];

  function verificationStillValid(email: string): boolean {
    return verified !== null && verified.email === email && Date.now() - verified.at < VERIFICATION_VALID_MS;
  }

  function handleAccount(values: AccountValues) {
    setServerErrors({});
    start.reset();
    setAccount(values);
    if (verificationStillValid(values.email)) {
      setStep('profile'); // came back to fix something else; the same address is still verified
      return;
    }
    start.mutate(values);
  }

  function handleProfile(values: ProfileValues, coords: Coordinates) {
    if (!account) return;
    setServerErrors({});
    setProfile(values);
    if (!verificationStillValid(account.email)) {
      setNotice('Your email verification has expired. Verify your address again to finish.');
      setStep('verify');
      return;
    }
    create.mutate({ account, profile: values, coords });
  }

  const profileError =
    create.isError && step === 'profile' && !(create.error instanceof ApiError && Object.keys(create.error.fieldErrors).length > 0)
      ? toUserMessage(create.error, "We couldn't create your account. Please try again.")
      : null;

  return (
    <div className="container auth-page auth-page--wide">
      <Stepper steps={STEPS.map((item) => item.label)} current={currentIndex} />
      <h1 className="auth-page__title">{current?.title}</h1>

      {step === 'account' && (
        <>
          <AccountStep
            defaultValues={account}
            serverErrors={serverErrors}
            submitError={start.error}
            submitting={start.isPending}
            onSubmit={handleAccount}
          />
          <p className="auth-page__footnote">
            Already have an account? <Link to={ROUTES.login}>Log in</Link>
          </p>
        </>
      )}

      {step === 'verify' && account && (
        <VerifyStep
          email={account.email}
          notice={notice}
          codeSentAt={codeSentAt}
          onCodeSent={setCodeSentAt}
          onVerified={() => {
            setVerified({ email: account.email, at: Date.now() });
            setNotice(null);
            setStep('profile');
          }}
          onBack={() => setStep('account')}
        />
      )}

      {step === 'profile' && account && (
        <ProfileStep
          defaultValues={profile}
          location={location}
          serverErrors={serverErrors}
          submitError={profileError}
          submitting={create.isPending}
          onBack={() => setStep('account')}
          onSubmit={handleProfile}
        />
      )}

      {step === 'photos' && (
        <PhotosStep
          onFinish={() => {
            const role = state.status === 'authenticated' ? state.session.role : Role.USER;
            navigate(roleHome(role), { replace: true });
          }}
        />
      )}
    </div>
  );
}
