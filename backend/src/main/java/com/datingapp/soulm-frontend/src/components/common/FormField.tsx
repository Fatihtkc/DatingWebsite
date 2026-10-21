import { forwardRef, useId, useState, type InputHTMLAttributes } from 'react';
import { cx } from '@/utils/cx';

interface FormFieldProps extends Omit<InputHTMLAttributes<HTMLInputElement>, 'id'> {
  label: string;
  error?: string | undefined;
  hint?: string;
}

/** Labelled input with hint, error and (for type="password") a show/hide control. Works with react-hook-form's register(). */
export const FormField = forwardRef<HTMLInputElement, FormFieldProps>(function FormField(
  { label, error, hint, type = 'text', className, ...rest },
  ref,
) {
  const id = useId();
  const hintId = `${id}-hint`;
  const errorId = `${id}-error`;
  const isPassword = type === 'password';
  const [revealed, setRevealed] = useState(false);
  const describedBy = [hint ? hintId : null, error ? errorId : null].filter(Boolean).join(' ') || undefined;

  return (
    <div className={cx('field', error && 'field--invalid', className)}>
      <label className="field__label" htmlFor={id}>
        {label}
      </label>
      <div className="field__control">
        <input
          ref={ref}
          id={id}
          type={isPassword && revealed ? 'text' : type}
          aria-invalid={error ? true : undefined}
          aria-describedby={describedBy}
          {...rest}
        />
        {isPassword && (
          <button
            type="button"
            className="field__toggle"
            aria-pressed={revealed}
            onClick={() => setRevealed((value) => !value)}
          >
            {revealed ? 'Hide' : 'Show'}
            <span className="visually-hidden"> password</span>
          </button>
        )}
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
