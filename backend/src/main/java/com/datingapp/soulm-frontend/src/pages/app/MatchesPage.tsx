import { useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { toUserMessage } from '@/api/errors';
import { Button } from '@/components/common/Button';
import { ConfirmDialog } from '@/components/common/ConfirmDialog';
import { EmptyState, ErrorState, LoadingState, LoadMore } from '@/components/common/StateViews';
import { PersonCard } from '@/components/dating/PersonCard';
import { deleteMatch } from '@/features/matches/matchesApi';
import { useMatchesQuery } from '@/features/matches/useMatchesQuery';
import { usePageTitle } from '@/hooks/usePageTitle';
import { APP_ROUTES } from '@/routes';
import type { Match } from '@/types/social';
import { displayName } from '@/utils/format';

export function MatchesPage() {
  usePageTitle('Matches');
  const queryClient = useQueryClient();
  const matches = useMatchesQuery();
  const [target, setTarget] = useState<Match | null>(null);

  const unmatch = useMutation({
    mutationFn: (match: Match) => deleteMatch(match.id),
    onSuccess: () => {
      setTarget(null);
      void queryClient.invalidateQueries({ queryKey: ['matches'] });
    },
  });

  const items = matches.data?.pages.flatMap((page) => page.content) ?? [];

  function close() {
    if (!unmatch.isPending) {
      setTarget(null);
      unmatch.reset();
    }
  }

  return (
    <div className="container page">
      <h1 className="page__title">Your matches</h1>

      {matches.isPending ? (
        <LoadingState label="Loading your matches" />
      ) : matches.isError ? (
        <ErrorState error={matches.error} onRetry={() => void matches.refetch()} fallback="We couldn't load your matches." />
      ) : items.length === 0 ? (
        <EmptyState
          title="No matches yet"
          action={
            <Link to={APP_ROUTES.discover} className="btn btn--primary">
              Go to Discover
            </Link>
          }
        >
          When you like someone and they like you back, they will appear here.
        </EmptyState>
      ) : (
        <>
          <ul className="people">
            {items.map((match) => (
              <PersonCard
                key={match.id}
                profile={match.user}
                detail={`Matched on ${new Date(match.matchedAt).toLocaleDateString()}`}
                actions={
                  <>
                    <Link to={APP_ROUTES.conversation(match.user.id)} className="btn btn--primary">
                      Message
                    </Link>
                    <Button variant="ghost" onClick={() => setTarget(match)}>
                      Unmatch
                    </Button>
                  </>
                }
              />
            ))}
          </ul>
          <LoadMore hasMore={matches.hasNextPage} loading={matches.isFetchingNextPage} onClick={() => void matches.fetchNextPage()} />
        </>
      )}

      <ConfirmDialog
        open={target !== null}
        title="Unmatch?"
        confirmLabel="Unmatch"
        destructive
        busy={unmatch.isPending}
        error={unmatch.isError ? toUserMessage(unmatch.error, "We couldn't unmatch. Please try again.") : null}
        onConfirm={() => target && unmatch.mutate(target)}
        onCancel={close}
      >
        {target && (
          <p>
            You and {displayName(target.user)} will no longer be able to message each other. Your earlier messages are not deleted.
          </p>
        )}
      </ConfirmDialog>
    </div>
  );
}
