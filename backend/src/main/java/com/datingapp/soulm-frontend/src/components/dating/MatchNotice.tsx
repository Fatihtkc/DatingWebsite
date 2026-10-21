import { Link } from 'react-router-dom';
import { Alert } from '@/components/common/Alert';
import { APP_ROUTES } from '@/routes';

export type LikeNotice =
  | { kind: 'matched'; userId: number; name: string }
  | { kind: 'liked'; name: string; matchCheckFailed: boolean }
  | { kind: 'already-liked'; name: string }
  | { kind: 'error'; text: string };

/** Result of a like, announced politely to assistive technology. */
export function MatchNotice({ notice }: { notice: LikeNotice | null }) {
  return (
    <div aria-live="polite">
      {notice?.kind === 'matched' && (
        <Alert tone="success">
          <strong>It&rsquo;s a match with {notice.name}!</strong> You can message each other now.{' '}
          <Link to={APP_ROUTES.conversation(notice.userId)}>Say hello</Link>
        </Alert>
      )}
      {notice?.kind === 'liked' && (
        <Alert tone="info">
          You liked {notice.name}.
          {notice.matchCheckFailed && ' We could not check for a match just now; it will show under Matches if they like you back.'}
        </Alert>
      )}
      {notice?.kind === 'already-liked' && <Alert tone="info">You already liked {notice.name}.</Alert>}
      {notice?.kind === 'error' && <Alert tone="error">{notice.text}</Alert>}
    </div>
  );
}
