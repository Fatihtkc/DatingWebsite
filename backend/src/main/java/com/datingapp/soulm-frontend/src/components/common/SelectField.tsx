import { forwardRef, useId, type SelectHTMLAttributes } from 'react';
import { cx } from '@/utils/cx';

interface SelectFieldProps extends Omit<SelectHTMLAttributes<HTMLSelectElement>, 'id'> {
  label: string;
  options: ReadonlyArray<{ value: string; label: string }>;
  /** Text of the empty first option. */
  placeholder?: string;
  error?: string | undefined;
  hint?: string;
}

export const SelectField = forwardRef<HTMLSelectElement, SelectFieldProps>(function SelectField(
  { label, options, placeholder = 'Choose\u2026', error, hint, className, ...rest },
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
        <select ref={ref} id={id} aria-invalid={error ? true : undefined} aria-describedby={describedBy} {...rest}>
          <option value="">{placeholder}</option>
          {options.map((option) => (
            <option key={option.value} value={option.value}>
              {option.label}
            </option>
          ))}
        </select>
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
