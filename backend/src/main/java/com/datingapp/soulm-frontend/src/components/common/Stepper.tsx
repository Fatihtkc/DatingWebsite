import { cx } from '@/utils/cx';

interface StepperProps {
  steps: readonly string[];
  /** Zero-based index of the current step. */
  current: number;
}

/** A real sequence, so the numbering carries meaning. */
export function Stepper({ steps, current }: StepperProps) {
  return (
    <nav aria-label="Sign-up progress">
      <ol className="stepper">
        {steps.map((label, index) => (
          <li
            key={label}
            className={cx('stepper__item', index < current && 'is-done', index === current && 'is-current')}
            aria-current={index === current ? 'step' : undefined}
          >
            <span className="stepper__index" aria-hidden="true">
              {index < current ? '\u2713' : index + 1}
            </span>
            <span className="stepper__label">
              {label}
              {index < current && <span className="visually-hidden"> (completed)</span>}
            </span>
          </li>
        ))}
      </ol>
    </nav>
  );
}
