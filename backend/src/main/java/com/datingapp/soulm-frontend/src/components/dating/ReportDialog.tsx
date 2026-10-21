import { useMemo, useState } from 'react';
import { useMutation } from '@tanstack/react-query';
import { toUserMessage } from '@/api/errors';
import { Alert } from '@/components/common/Alert';
import { Button } from '@/components/common/Button';
import { Modal } from '@/components/common/Modal';
import { PhotoPicker } from '@/components/common/PhotoPicker';
import { TextAreaField } from '@/components/common/TextAreaField';
import { createComplaint } from '@/features/reports/reportApi';
import { checkPhotos } from '@/features/profile/photoRules';

const MIN_REASON = 10;
const MAX_REASON = 1000;
const MAX_EVIDENCE = 5;

interface ReportDialogProps {
  open: boolean;
  userId: number;
  name: string;
  onClose: () => void;
}

function ReportForm({ userId, name, onClose }: Omit<ReportDialogProps, 'open'>) {
  const [reason, setReason] = useState('');
  const [files, setFiles] = useState<File[]>([]);
  const problems = useMemo(() => checkPhotos(files, MAX_EVIDENCE), [files]);
  const trimmed = reason.trim();
  const reasonError =
    trimmed.length > 0 && trimmed.length < MIN_REASON ? `Please write at least ${MIN_REASON} characters.` : undefined;

  const report = useMutation({ mutationFn: () => createComplaint({ complainedId: userId, reason: trimmed }, files) });

  if (report.isSuccess) {
    return (
      <>
        <Alert tone="success">Thank you. A moderator will review your report.</Alert>
        <div className="modal__actions">
          <Button onClick={onClose}>Close</Button>
        </div>
      </>
    );
  }

  return (
    <form
      noValidate
      onSubmit={(event) => {
        event.preventDefault();
        if (trimmed.length >= MIN_REASON && problems.length === 0) report.mutate();
      }}
    >
      <p className="modal__body">
        Tell us what happened with {name}. Reports go to our moderators; {name} is not told who reported them.
      </p>
      {report.isError && <Alert tone="error">{toUserMessage(report.error, "We couldn't send your report. Please try again.")}</Alert>}
      <TextAreaField
        label="What happened?"
        rows={5}
        maxLength={MAX_REASON}
        hint={`${MIN_REASON} to ${MAX_REASON} characters.`}
        value={reason}
        onChange={(event) => setReason(event.target.value)}
        error={reasonError}
      />
      <p className="field__label">Screenshots (optional, up to {MAX_EVIDENCE})</p>
      <PhotoPicker files={files} problems={problems} onChange={setFiles} label="Add screenshots" />
      <div className="modal__actions">
        <Button variant="secondary" onClick={onClose} disabled={report.isPending}>
          Cancel
        </Button>
        <Button type="submit" loading={report.isPending} disabled={trimmed.length < MIN_REASON || problems.length > 0}>
          Send report
        </Button>
      </div>
    </form>
  );
}

export function ReportDialog({ open, userId, name, onClose }: ReportDialogProps) {
  return (
    <Modal open={open} title={`Report ${name}`} onClose={onClose}>
      <ReportForm userId={userId} name={name} onClose={onClose} />
    </Modal>
  );
}
