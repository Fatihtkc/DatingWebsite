import { useMemo, useState } from 'react';
import { useMutation } from '@tanstack/react-query';
import { toUserMessage } from '@/api/errors';
import { uploadMyImages } from '@/api/userApi';
import { Alert } from '@/components/common/Alert';
import { Button } from '@/components/common/Button';
import { PhotoPicker } from '@/components/common/PhotoPicker';
import { checkPhotos, MAX_PHOTOS } from '@/features/profile/photoRules';

interface PhotosStepProps {
  onFinish: () => void;
}

export function PhotosStep({ onFinish }: PhotosStepProps) {
  const [files, setFiles] = useState<File[]>([]);
  const problems = useMemo(() => checkPhotos(files), [files]);
  const upload = useMutation({ mutationFn: () => uploadMyImages(files) });

  if (upload.isSuccess) {
    const { accepted, rejected } = upload.data;
    return (
      <div className="auth-card">
        {accepted > 0 && <Alert tone="success">{accepted === 1 ? '1 photo was added.' : `${accepted} photos were added.`}</Alert>}
        {rejected > 0 && (
          <Alert tone="error">
            {rejected === 1 ? '1 photo was' : `${rejected} photos were`} skipped because it didn&rsquo;t pass our checks (content rules or file type).
          </Alert>
        )}
        <p>Photos are screened automatically before they appear on your profile.</p>
        <Button onClick={onFinish}>Continue</Button>
      </div>
    );
  }

  return (
    <div className="auth-card">
      <p>
        Add up to {MAX_PHOTOS} photos (JPG or PNG, 5 MB each). Photos are screened automatically before they appear on your profile.
      </p>
      {upload.isError && <Alert tone="error">{toUserMessage(upload.error, "We couldn't upload your photos. Please try again.")}</Alert>}
      <PhotoPicker
        files={files}
        problems={problems}
        onChange={(next) => {
          upload.reset();
          setFiles(next);
        }}
      />
      <div className="auth-page__actions">
        <Button loading={upload.isPending} disabled={files.length === 0 || problems.length > 0} onClick={() => upload.mutate()}>
          Upload photos
        </Button>
        <Button variant="ghost" onClick={onFinish}>
          Skip for now
        </Button>
      </div>
    </div>
  );
}
