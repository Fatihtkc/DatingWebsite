import { toUserMessage } from '@/api/errors';
import { ConfirmDialog } from '@/components/common/ConfirmDialog';
import { useBlockUser } from '@/features/blocks/useBlockUser';

interface BlockDialogProps {
  open: boolean;
  userId: number;
  name: string;
  onClose: () => void;
  onBlocked: () => void;
}

export function BlockDialog({ open, userId, name, onClose, onBlocked }: BlockDialogProps) {
  const block = useBlockUser();

  function close() {
    if (!block.isPending) {
      block.reset();
      onClose();
    }
  }

  return (
    <ConfirmDialog
      open={open}
      title={`Block ${name}?`}
      confirmLabel="Block"
      destructive
      busy={block.isPending}
      error={block.isError ? toUserMessage(block.error, "We couldn't block this person. Please try again.") : null}
      onConfirm={() => block.mutate(userId, { onSuccess: onBlocked })}
      onCancel={close}
    >
      <p>
        {name} will disappear from your results and your match with them, if any, will be removed. Neither of you will be able to message or like the other.
        You can unblock them later under Settings.
      </p>
    </ConfirmDialog>
  );
}
