import { useEffect, useMemo, type ChangeEvent } from 'react';
import { Alert } from './Alert';

interface PhotoPickerProps {
  files: readonly File[];
  onChange: (files: File[]) => void;
  /** Messages from checkPhotos(); an empty array means the selection is acceptable. */
  problems: readonly string[];
  label?: string;
}

/** File chooser with removable thumbnails. The parent owns the file list and the validation. */
export function PhotoPicker({ files, onChange, problems, label = 'Choose photos' }: PhotoPickerProps) {
  const previews = useMemo(() => files.map((file) => ({ file, url: URL.createObjectURL(file) })), [files]);
  useEffect(() => () => previews.forEach((preview) => URL.revokeObjectURL(preview.url)), [previews]);

  function onPick(event: ChangeEvent<HTMLInputElement>) {
    const picked = Array.from(event.target.files ?? []);
    event.target.value = ''; // so the same file can be chosen again after removing it
    onChange([...files, ...picked]);
  }

  return (
    <div className="photo-picker">
      {problems.length > 0 && (
        <Alert tone="error">
          {problems.map((problem) => (
            <div key={problem}>{problem}</div>
          ))}
        </Alert>
      )}
      <label className="btn btn--secondary file-picker">
        {label}
        <input type="file" accept="image/jpeg,image/png" multiple className="visually-hidden" onChange={onPick} />
      </label>
      {previews.length > 0 && (
        <ul className="photo-grid">
          {previews.map(({ file, url }, index) => (
            <li key={`${file.name}-${file.size}-${index}`} className="photo-grid__item">
              <img src={url} alt={`Selected photo ${index + 1} of ${previews.length}`} />
              <button
                type="button"
                className="photo-grid__remove"
                aria-label={`Remove photo ${index + 1}`}
                onClick={() => onChange(files.filter((_, i) => i !== index))}
              >
                &times;
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
