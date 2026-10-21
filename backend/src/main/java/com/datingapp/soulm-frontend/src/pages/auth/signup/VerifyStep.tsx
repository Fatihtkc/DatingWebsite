import { zodResolver } from '@hookform/resolvers/zod';
import { useMutation } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { normalizeApiError, toUserMessage } from '@/api/errors';
import { sendVerificationCode, verifyOtp } from '@/api/verificationApi';
import { Alert } from '@/components/common/Alert';
import { Button } from '@/components/common/Button';
import { FormField } from '@/components/common/FormField';
import { codeSchema } from '@/features/auth/schemas';
import { useCountdown } from '@/hooks/useCountdown';

/** OtpSendRateLimiter: at least 60 s between codes for the same address. */
export const RESEND_COOLDOWN_MS = 60_000;

interface VerifyStepProps {
  email: string;
  /** Why the person is here again (for example, the earlier verification expired). */
  notice: string | null;
  /** When the last code was sent (epoch ms); drives the resend cooldown. */
  codeSentAt: number | null;
  onCodeSent: (sentAt: number) => void;
  onVerified: () => void;
  onBack: () => void;
}

function verifyMessage(error: unknown): string {
  const apiError = normalizeApiError(error);
  if (apiError.status === 401 && apiError.code === 'INVALID_OTP') {
    return 'That code is incorrect or has expired. Check the code in your email, or send a new one.';
  }
  return toUserMessage(apiError, "We couldn't check that code. Please try again.");
}

export function VerifyStep({ email, notice, codeSentAt, onCodeSent, onVerified, onBack }: VerifyStepProps) {
  const secondsLeft = useCountdown(codeSentAt === null ? null : codeSentAt + RESEND_COOLDOWN_MS);

  const verify = useMutation({
    mutationFn: (code: string) => verifyOtp(email, code),
    onSuccess: () => onVerified(),
  });
  const resend = useMutation({
    mutationFn: () => sendVerificationCode(email),
    onSuccess: () => {
      verify.reset();
      onCodeSent(Date.now());
    },
  });

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<{ code: string }>({ resolver: zodResolver(codeSchema), defaultValues: { code: '' } });

  return (
    <form className="auth-card" noValidate onSubmit={handleSubmit((values) => verify.mutate(values.code))}>
      {notice && <Alert tone="info">{notice}</Alert>}
      <p>
        We sent a 6-digit code to <strong>{email}</strong>. It works for 10 minutes, and you get 5 tries before you need a new one.
      </p>
      {verify.isError && <Alert tone="error">{verifyMessage(verify.error)}</Alert>}
      {resend.isError && <Alert tone="error">{toUserMessage(resend.error, "We couldn't send a new code. Please try again.")}</Alert>}
      {resend.isSuccess && !verify.isError && <Alert tone="success">A new code is on its way.</Alert>}

      <FormField
        label="Verification code"
        inputMode="numeric"
        autoComplete="one-time-code"
        maxLength={6}
        error={errors.code?.message}
        {...register('code')}
      />

      <div className="auth-page__actions">
        <Button type="submit" loading={verify.isPending}>
          Verify email
        </Button>
        <Button variant="secondary" loading={resend.isPending} disabled={secondsLeft > 0} onClick={() => resend.mutate()}>
          {secondsLeft > 0 ? `Send a new code in ${secondsLeft}s` : 'Send a new code'}
        </Button>
        <Button variant="ghost" onClick={onBack}>
          Change email
        </Button>
      </div>
    </form>
  );
}
