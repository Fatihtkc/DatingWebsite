import { zodResolver } from '@hookform/resolvers/zod';
import { useMutation } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { Link } from 'react-router-dom';
import { ApiError, normalizeApiError, toUserMessage } from '@/api/errors';
import { Alert } from '@/components/common/Alert';
import { Button } from '@/components/common/Button';
import { FormField } from '@/components/common/FormField';
import * as authApi from '@/features/auth/authApi';
import { loginSchema, type LoginValues } from '@/features/auth/schemas';
import { useAuth } from '@/features/auth/useAuth';
import { usePageTitle } from '@/hooks/usePageTitle';
import { ROUTES } from '@/routes';

function loginErrorMessage(error: unknown): string {
  const apiError = normalizeApiError(error);
  // AuthenticationController lets BadCredentialsException through; once it is mapped to 401 this is the wrong-password case.
  if (apiError.kind === 'http' && apiError.status === 401) {
    return "That email and password don't match. Check them and try again.";
  }
  return toUserMessage(apiError, "We couldn't log you in. Please try again.");
}

export function LoginPage() {
  usePageTitle('Log in');
  const { state, signIn } = useAuth();

  // On success signIn() flips the auth state and <PublicOnly> performs the redirect (to the page the
  // person came from if it is theirs to open, otherwise their role's home).
  const mutation = useMutation({
    mutationFn: (values: LoginValues) => authApi.login(values.email, values.password),
    onSuccess: (result) => signIn(result),
  });

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginValues>({ resolver: zodResolver(loginSchema), defaultValues: { email: '', password: '' } });

  const fieldErrors: Readonly<Record<string, string>> = mutation.error instanceof ApiError ? mutation.error.fieldErrors : {};
  const hasFieldErrors = Object.keys(fieldErrors).length > 0;
  const expired = state.status === 'anonymous' && state.expired;

  return (
    <div className="container auth-page">
      <h1 className="auth-page__title">Log in</h1>
      <p className="auth-page__lead">Welcome back. Use the email address and password you signed up with.</p>

      <form className="auth-card" noValidate onSubmit={handleSubmit((values) => mutation.mutate(values))}>
        {expired && !mutation.isError && <Alert tone="info">Your session has ended. Log in again to continue.</Alert>}
        {mutation.isError && !hasFieldErrors && <Alert tone="error">{loginErrorMessage(mutation.error)}</Alert>}
        <FormField
          label="Email address"
          type="email"
          autoComplete="email"
          inputMode="email"
          error={errors.email?.message ?? fieldErrors.email}
          {...register('email')}
        />
        <FormField
          label="Password"
          type="password"
          autoComplete="current-password"
          error={errors.password?.message ?? fieldErrors.password}
          {...register('password')}
        />
        <Button type="submit" loading={mutation.isPending}>
          Log in
        </Button>
        <Link to={ROUTES.forgotPassword}>Forgot your password?</Link>
      </form>

      <p className="auth-page__footnote">
        New to SoulM? <Link to={ROUTES.signup}>Create your profile</Link>
      </p>
    </div>
  );
}
