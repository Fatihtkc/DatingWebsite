import type { ReactNode } from 'react';
import { toUserMessage } from '@/api/errors';
import { Alert } from './Alert';
import { Button } from './Button';

export function LoadingState({ label = 'Loading' }: { label?: string }) {
  return (
    <div className="state" role="status" aria-live="polite">
      <span className="btn__spinner state__spinner" aria-hidden="true" />
      <p>{label}&hellip;</p>
    </div>
  );
}

export function EmptyState({ title, children, action }: { title: string; children?: ReactNode; action?: ReactNode }) {
  return (
    <div className="state">
      <h2 className="state__title">{title}</h2>
      {children && <p>{children}</p>}
      {action}
    </div>
  );
}

export function ErrorState({ error, onRetry, fallback }: { error: unknown; onRetry?: () => void; fallback?: string }) {
  return (
    <div className="state">
      <Alert tone="error">{toUserMessage(error, fallback)}</Alert>
      {onRetry && <Button variant="secondary" onClick={onRetry}>Try again</Button>}
    </div>
  );
}

export function LoadMore({ hasMore, loading, onClick }: { hasMore: boolean; loading: boolean; onClick: () => void }) {
  if (!hasMore) return null;
  return (
    <div className="load-more">
      <Button variant="secondary" loading={loading} onClick={onClick}>
        Load more
      </Button>
    </div>
  );
}
