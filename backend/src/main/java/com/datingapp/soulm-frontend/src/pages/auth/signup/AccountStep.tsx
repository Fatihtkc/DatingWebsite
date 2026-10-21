import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { Link } from 'react-router-dom';
import { ApiError, toUserMessage } from '@/api/errors';
import { Alert } from '@/components/common/Alert';
import { Button } from '@/components/common/Button';
import { FormField } from '@/components/common/FormField';
import { accountSchema, PASSWORD_HINT, type AccountValues } from '@/features/auth/schemas';
import { ROUTES } from '@/routes';
import { latestAdultBirthDate } from '@/utils/date';

const EMPTY: AccountValues = {
  firstName: '',
  lastName: '',
  username: '',
  email: '',
  confirmEmail: '',
  birthDate: '',
  password: '',
  confirmPassword: '',
};

interface AccountStepProps {
  defaultValues: AccountValues | null;
  /** Field messages from a 400 VALIDATION_FAILED returned by the final signup call. */
  serverErrors: Record<string, string>;
  /** Failure of the "check availability, then send the code" action. */
  submitError: unknown;
  submitting: boolean;
  onSubmit: (values: AccountValues) => void;
}

/** UserServiceImpl#checkUser reports one conflict at a time: "Email is already in use" or "Username is already in use". */
function duplicateField(error: unknown): 'email' | 'username' | null {
  if (!(error instanceof ApiError) || error.status !== 409) return null;
  if (/^email/i.test(error.message)) return 'email';
  if (/^username/i.test(error.message)) return 'username';
  return null;
}

export function AccountStep({ defaultValues, serverErrors, submitError, submitting, onSubmit }: AccountStepProps) {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<AccountValues>({ resolver: zodResolver(accountSchema), defaultValues: defaultValues ?? EMPTY });

  const duplicate = duplicateField(submitError);
  const emailError =
    errors.email?.message ?? serverErrors.email ?? (duplicate === 'email' ? 'An account with this email address already exists.' : undefined);
  const usernameError =
    errors.username?.message ?? serverErrors.username ?? (duplicate === 'username' ? 'That username is taken. Try another one.' : undefined);

  return (
    <form className="auth-card" noValidate onSubmit={handleSubmit(onSubmit)}>
      {submitError !== null && submitError !== undefined && duplicate === null && (
        <Alert tone="error">{toUserMessage(submitError, "We couldn't continue. Please try again.")}</Alert>
      )}
      {duplicate === 'email' && (
        <Alert tone="info">
          Already registered? <Link to={ROUTES.login}>Log in instead</Link>.
        </Alert>
      )}

      <div className="form-grid">
        <FormField label="First name" autoComplete="given-name" error={errors.firstName?.message ?? serverErrors.firstName} {...register('firstName')} />
        <FormField label="Last name" autoComplete="family-name" error={errors.lastName?.message ?? serverErrors.lastName} {...register('lastName')} />
      </div>
      <FormField
        label="Username"
        autoComplete="username"
        autoCapitalize="none"
        spellCheck={false}
        hint="3 to 30 characters. This is what other people see."
        error={usernameError}
        {...register('username')}
      />
      <FormField label="Email address" type="email" autoComplete="email" inputMode="email" error={emailError} {...register('email')} />
      <FormField
        label="Confirm email address"
        type="email"
        autoComplete="off"
        inputMode="email"
        error={errors.confirmEmail?.message}
        {...register('confirmEmail')}
      />
      <FormField
        label="Date of birth"
        type="date"
        autoComplete="bday"
        min="1900-01-01"
        max={latestAdultBirthDate()}
        hint="You must be 18 or older. Other people see your age, not your birth date."
        error={errors.birthDate?.message ?? serverErrors.birthDate}
        {...register('birthDate')}
      />
      <FormField
        label="Password"
        type="password"
        autoComplete="new-password"
        hint={PASSWORD_HINT}
        error={errors.password?.message ?? serverErrors.password}
        {...register('password')}
      />
      <FormField
        label="Confirm password"
        type="password"
        autoComplete="new-password"
        error={errors.confirmPassword?.message}
        {...register('confirmPassword')}
      />
      <Button type="submit" loading={submitting}>
        Continue
      </Button>
    </form>
  );
}
