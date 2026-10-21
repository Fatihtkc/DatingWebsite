import { forwardRef, useId, type TextareaHTMLAttributes } from 'react';
import { cx } from '@/utils/cx';

interface TextAreaFieldProps extends Omit<TextareaHTMLAttributes<HTMLTextAreaElement>, 'id'> {
  label: string;
  error?: string | undefined;
  hint?: string;
}

export const TextAreaField = forwardRef<HTMLTextAreaElement, TextAreaFieldProps>(function TextAreaField(
  { label, error, hint, className, rows = 3, ...rest },
  ref,
) {
  const id = useId();
  const hintId = `${id}-hint`;
  const errorId = `${id}-error`;
  const describedBy = [hint ? hintId : null, error ? errorId : null].filter(Boolean).join(' ') || undefined;

  return (
    <div className={cx('field', error && 'field--invalid', className)}>
      <label className="field__label" htmlFor={id}>
        {label}
      </label>
      <div className="field__control">
        <textarea ref={ref} id={id} rows={rows} aria-invalid={error ? true : undefined} aria-describedby={describedBy} {...rest} />
      </div>
      {hint && (
        <p id={hintId} className="field__hint">
          {hint}
        </p>
      )}
      {error && (
        <p id={errorId} className="field__error" role="alert">
          {error}
        </p>
      )}
    </div>
  );
});
