import { useState } from 'react';
import { zodResolver } from '@hookform/resolvers/zod';
import { useMutation } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { Link } from 'react-router-dom';
import { z } from 'zod';
import { requestPasswordReset } from '@/api/verificationApi';
import { toUserMessage } from '@/api/errors';
import { Alert } from '@/components/common/Alert';
import { Button } from '@/components/common/Button';
import { FormField } from '@/components/common/FormField';
import { usePageTitle } from '@/hooks/usePageTitle';
import { ROUTES } from '@/routes';

const schema = z.object({
  email: z.string().trim().min(1, 'Enter your email address.').email('Enter a valid email address.'),
});
type FormValues = z.infer<typeof schema>;

export function ForgotPasswordPage() {
  usePageTitle('Reset your password');
  const [sentTo, setSentTo] = useState<string | null>(null);

  const mutation = useMutation({
    mutationFn: (email: string) => requestPasswordReset(email),
    onSuccess: (_data, email) => setSentTo(email),
  });

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormValues>({ resolver: zodResolver(schema), defaultValues: { email: '' } });

  if (sentTo) {
    return (
      <div className="container auth-page">
        <h1 className="auth-page__title">Check your email</h1>
        {/* Deliberately neutral: the backend answers identically whether or not the account exists. */}
        <p className="auth-page__lead">
          If an account exists for <strong>{sentTo}</strong>, we have sent a link to reset its password. The link works
          for 15 minutes.
        </p>
        <p className="auth-page__lead">Nothing in your inbox? Check your spam folder, or try again.</p>
        <div className="auth-page__actions">
          <Button
            variant="secondary"
            onClick={() => {
              mutation.reset();
              setSentTo(null);
            }}
          >
            Use a different email
          </Button>
          <Link to={ROUTES.login} className="btn btn--ghost">
            Back to log in
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="container auth-page">
      <h1 className="auth-page__title">Reset your password</h1>
      <p className="auth-page__lead">Enter the email address you signed up with and we will send you a reset link.</p>

      <form className="auth-card" noValidate onSubmit={handleSubmit((values) => mutation.mutate(values.email))}>
        {mutation.isError && <Alert tone="error">{toUserMessage(mutation.error)}</Alert>}
        <FormField
          label="Email address"
          type="email"
          autoComplete="email"
          inputMode="email"
          error={errors.email?.message}
          {...register('email')}
        />
        <Button type="submit" loading={mutation.isPending}>
          Send reset link
        </Button>
      </form>

      <p className="auth-page__footnote">
        Remembered it? <Link to={ROUTES.login}>Back to log in</Link>
      </p>
    </div>
  );
}
