import { zodResolver } from '@hookform/resolvers/zod';
import { useMutation } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { Link } from 'react-router-dom';
import { z } from 'zod';
import { ApiError, toUserMessage } from '@/api/errors';
import { Alert } from '@/components/common/Alert';
import { Button } from '@/components/common/Button';
import { FormField } from '@/components/common/FormField';
import { useAuth } from '@/features/auth/useAuth';
import { PASSWORD_HINT, passwordSchema } from '@/features/auth/schemas';
import { changeMyStaffPassword } from '@/features/staff/staffApi';
import { usePageTitle } from '@/hooks/usePageTitle';
import { Role } from '@/types/enums';

// Staff accounts are created under the same password rule as members (ModeratorCreateDTO / ManagerCreateDTO).
// PasswordChangeRequestDTO's own rules were not supplied.
const schema = z
  .object({
    oldPassword: z.string().min(1, 'Enter your current password.'),
    newPassword: passwordSchema,
    confirmPassword: z.string().min(1, 'Re-enter your new password.'),
  })
  .refine((values) => values.newPassword === values.confirmPassword, { path: ['confirmPassword'], message: 'The two passwords do not match.' })
  .refine((values) => values.newPassword !== values.oldPassword, { path: ['newPassword'], message: 'Choose a password you are not already using.' });
type FormValues = z.infer<typeof schema>;

export function StaffPasswordPage({ backTo }: { backTo: string }) {
  usePageTitle('Change password');
  const { state } = useAuth();
  const role = state.status === 'authenticated' ? state.session.role : null;

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<FormValues>({ resolver: zodResolver(schema), defaultValues: { oldPassword: '', newPassword: '', confirmPassword: '' } });

  const change = useMutation({
    mutationFn: (values: FormValues) => {
      if (role !== Role.MODERATOR && role !== Role.MANAGER) throw new Error('Not a staff account.');
      return changeMyStaffPassword(role, { oldPassword: values.oldPassword, newPassword: values.newPassword });
    },
    onSuccess: () => reset(),
  });

  const fieldErrors: Readonly<Record<string, string>> = change.error instanceof ApiError ? change.error.fieldErrors : {};
  // InvalidPasswordException ("Old password is incorrect.") arrives as 400 INVALID_PASSWORD.
  const wrongOld = change.error instanceof ApiError && change.error.code === 'INVALID_PASSWORD';

  return (
    <div className="container auth-page">
      <h1 className="auth-page__title">Change password</h1>
      <form className="auth-card" noValidate onSubmit={handleSubmit((values) => change.mutate(values))}>
        {change.isSuccess && <Alert tone="success">Your password was changed.</Alert>}
        {change.isError && !wrongOld && Object.keys(fieldErrors).length === 0 && (
          <Alert tone="error">{toUserMessage(change.error, "We couldn't change your password. Please try again.")}</Alert>
        )}
        <FormField
          label="Current password"
          type="password"
          autoComplete="current-password"
          error={errors.oldPassword?.message ?? (wrongOld ? 'That is not your current password.' : fieldErrors.oldPassword)}
          {...register('oldPassword')}
        />
        <FormField label="New password" type="password" autoComplete="new-password" hint={PASSWORD_HINT} error={errors.newPassword?.message ?? fieldErrors.newPassword} {...register('newPassword')} />
        <FormField label="Confirm new password" type="password" autoComplete="new-password" error={errors.confirmPassword?.message} {...register('confirmPassword')} />
        <Button type="submit" loading={change.isPending}>
          Change password
        </Button>
      </form>
      <Link to={backTo} className="btn btn--ghost">
        Back to my profile
      </Link>
    </div>
  );
}
