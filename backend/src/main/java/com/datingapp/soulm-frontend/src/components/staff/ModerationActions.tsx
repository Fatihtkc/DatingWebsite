import { useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { toUserMessage } from '@/api/errors';
import { Button } from '@/components/common/Button';
import { ConfirmDialog } from '@/components/common/ConfirmDialog';
import { approveUser, banUser } from '@/features/moderation/moderationApi';
import type { UserAdmin } from '@/types/account';

type Kind = 'approve' | 'ban';

interface ModerationActionsProps {
  user: Pick<UserAdmin, 'id' | 'approved' | 'banned'>;
  name: string;
  onUpdated?: (user: UserAdmin) => void;
}

/**
 * Approve and ban, each behind a confirmation. Note the backend's rule: "approve" sets approved=true AND
 * banned=false, so it is also how a ban is lifted. Only moderators may call these endpoints.
 */
export function ModerationActions({ user, name, onUpdated }: ModerationActionsProps) {
  const queryClient = useQueryClient();
  const [dialog, setDialog] = useState<Kind | null>(null);

  const act = useMutation({
    mutationFn: (kind: Kind) => (kind === 'approve' ? approveUser(user.id) : banUser(user.id)),
    onSuccess: (updated) => {
      setDialog(null);
      void queryClient.invalidateQueries({ queryKey: ['moderation'] });
      onUpdated?.(updated);
    },
  });

  function close() {
    if (!act.isPending) {
      setDialog(null);
      act.reset();
    }
  }

  const needsApproval = !user.approved;
  const showBan = !user.banned;

  return (
    <>
      {(needsApproval || user.banned) && (
        <Button onClick={() => setDialog('approve')}>{user.banned ? 'Lift ban' : 'Approve'}</Button>
      )}
      {showBan && (
        <Button variant="secondary" onClick={() => setDialog('ban')}>
          Ban
        </Button>
      )}

      <ConfirmDialog
        open={dialog === 'approve'}
        title={user.banned ? `Lift the ban on ${name}?` : `Approve ${name}?`}
        confirmLabel={user.banned ? 'Lift ban' : 'Approve'}
        busy={act.isPending}
        error={act.isError ? toUserMessage(act.error, "We couldn't complete that. Please try again.") : null}
        onConfirm={() => act.mutate('approve')}
        onCancel={close}
      >
        <p>
          {name} will appear in other people&rsquo;s results. Approving always also clears any ban on the account, so they can log in again.
        </p>
      </ConfirmDialog>
      <ConfirmDialog
        open={dialog === 'ban'}
        title={`Ban ${name}?`}
        confirmLabel="Ban"
        destructive
        busy={act.isPending}
        error={act.isError ? toUserMessage(act.error, "We couldn't complete that. Please try again.") : null}
        onConfirm={() => act.mutate('ban')}
        onCancel={close}
      >
        <p>
          {name} will be signed out and won&rsquo;t be able to log in or be seen by others. The server does not record a reason. You can lift the ban later by
          approving them.
        </p>
      </ConfirmDialog>
    </>
  );
}
