import { useEffect, useState } from 'react';
import { zodResolver } from '@hookform/resolvers/zod';
import { useMutation, useQuery } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { z } from 'zod';
import { ApiError, toUserMessage } from '@/api/errors';
import { resetPassword, validateResetToken } from '@/api/verificationApi';
import { Alert } from '@/components/common/Alert';
import { Button } from '@/components/common/Button';
import { FormField } from '@/components/common/FormField';
import { PASSWORD_HINT, passwordSchema } from '@/features/auth/schemas';
import { usePageTitle } from '@/hooks/usePageTitle';
import { ROUTES } from '@/routes';

// PasswordResetRequest's validation was not supplied. Use the signup policy (UserCreateDTO): it is the
// strictest rule the backend is known to apply to passwords, so anything accepted here should also pass there.
const schema = z
  .object({
    newPassword: passwordSchema,
    confirmPassword: z.string().min(1, 'Re-enter your new password.'),
  })
  .refine((values) => values.newPassword === values.confirmPassword, {
    path: ['confirmPassword'],
    message: 'The two passwords do not match.',
  });
type FormValues = z.infer<typeof schema>;

function LinkProblem({ children }: { children: string }) {
  return (
    <div className="container auth-page">
      <h1 className="auth-page__title">This reset link can&rsquo;t be used</h1>
      <p className="auth-page__lead">{children}</p>
      <Link to={ROUTES.forgotPassword} className="btn btn--primary">
        Request a new link
      </Link>
    </div>
  );
}

export function ResetPasswordPage() {
  usePageTitle('Choose a new password');
  const location = useLocation();
  const navigate = useNavigate();

  // The backend e-mails /reset-password?token=... Capture the token once, then strip it from the URL
  // so it does not linger in history, screenshots or shared links. It is never persisted.
  const [token] = useState(() => new URLSearchParams(window.location.search).get('token'));
  useEffect(() => {
    if (location.search) navigate({ search: '' }, { replace: true });
  }, [location.search, navigate]);

  const [done, setDone] = useState(false);
  const hasToken = token !== null && token.length > 0;

  const validation = useQuery({
    queryKey: ['password-reset-token', token],
    queryFn: ({ signal }) => validateResetToken(token ?? '', signal),
    enabled: hasToken && !done,
    retry: false,
    staleTime: Infinity,
    gcTime: 0, // keep the token out of the query cache once nobody is watching
  });

  const mutation = useMutation({
    mutationFn: (newPassword: string) => resetPassword(token ?? '', newPassword),
    onSuccess: () => setDone(true),
  });

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormValues>({ resolver: zodResolver(schema), defaultValues: { newPassword: '', confirmPassword: '' } });

  if (done) {
    return (
      <div className="container auth-page">
        <h1 className="auth-page__title">Password updated</h1>
        <p className="auth-page__lead">
          Log in with your new password. For your security you have been signed out on all devices.
        </p>
        <Link to={ROUTES.login} className="btn btn--primary">
          Go to log in
        </Link>
      </div>
    );
  }

  if (!hasToken) {
    return <LinkProblem>This page needs the link from your reset email. Request a new one and use the link in that message.</LinkProblem>;
  }

  if (validation.isPending) {
    return (
      <div className="container auth-page" aria-busy="true">
        <h1 className="auth-page__title">Checking your link</h1>
        <div className="skeleton skeleton--block" role="status">
          <span className="visually-hidden">Checking your reset link</span>
        </div>
      </div>
    );
  }

  if (validation.isError) {
    const error = validation.error;
    // The backend reports invalid, expired and already-used tokens identically (400).
    if (error instanceof ApiError && error.status === 400) {
      return <LinkProblem>This link has expired or has already been used. Reset links work once and for 15 minutes.</LinkProblem>;
    }
    return (
      <div className="container auth-page">
        <h1 className="auth-page__title">We couldn&rsquo;t check your link</h1>
        <Alert tone="error">{toUserMessage(error)}</Alert>
        <Button onClick={() => void validation.refetch()}>Try again</Button>
      </div>
    );
  }

  const submitError =
    mutation.error instanceof ApiError && mutation.error.status === 400
      ? 'This link has expired or has already been used. Request a new one to continue.'
      : mutation.isError
        ? toUserMessage(mutation.error)
        : null;

  return (
    <div className="container auth-page">
      <h1 className="auth-page__title">Choose a new password</h1>
      <form className="auth-card" noValidate onSubmit={handleSubmit((values) => mutation.mutate(values.newPassword))}>
        {submitError && <Alert tone="error">{submitError}</Alert>}
        <FormField
          label="New password"
          type="password"
          autoComplete="new-password"
          hint={PASSWORD_HINT}
          error={errors.newPassword?.message}
          {...register('newPassword')}
        />
        <FormField
          label="Confirm new password"
          type="password"
          autoComplete="new-password"
          error={errors.confirmPassword?.message}
          {...register('confirmPassword')}
        />
        <Button type="submit" loading={mutation.isPending}>
          Update password
        </Button>
      </form>
    </div>
  );
}
