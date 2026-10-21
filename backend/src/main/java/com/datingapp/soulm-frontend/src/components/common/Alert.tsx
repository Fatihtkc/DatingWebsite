import type { ReactNode } from 'react';
import { cx } from '@/utils/cx';

interface AlertProps {
  tone?: 'error' | 'success' | 'info';
  children: ReactNode;
  className?: string;
}

export function Alert({ tone = 'info', children, className }: AlertProps) {
  return (
    <div className={cx('alert', `alert--${tone}`, className)} role={tone === 'error' ? 'alert' : 'status'}>
      {children}
    </div>
  );
}
