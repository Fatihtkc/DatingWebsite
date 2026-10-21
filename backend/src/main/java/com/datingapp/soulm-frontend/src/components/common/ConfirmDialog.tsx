import type { ReactNode } from 'react';
import { Alert } from './Alert';
import { Button } from './Button';
import { Modal } from './Modal';

interface ConfirmDialogProps {
  open: boolean;
  title: string;
  confirmLabel: string;
  destructive?: boolean;
  busy?: boolean;
  /** Message from a failed attempt; the dialog stays open so the person can retry. */
  error?: string | null;
  onConfirm: () => void;
  onCancel: () => void;
  children: ReactNode;
}

/** For consequential actions: say what will happen, disable repeats while pending, keep the dialog open on failure. */
export function ConfirmDialog({ open, title, confirmLabel, destructive = false, busy = false, error, onConfirm, onCancel, children }: ConfirmDialogProps) {
  return (
    <Modal open={open} title={title} onClose={onCancel} locked={busy}>
      <div className="modal__body">{children}</div>
      {error && <Alert tone="error">{error}</Alert>}
      <div className="modal__actions">
        <Button variant="secondary" onClick={onCancel} disabled={busy}>
          Cancel
        </Button>
        <Button className={destructive ? 'btn--danger' : undefined} loading={busy} onClick={onConfirm}>
          {confirmLabel}
        </Button>
      </div>
    </Modal>
  );
}
