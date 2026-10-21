import { useEffect, useMemo, useRef, useState } from 'react';
import { useInfiniteQuery, useQuery } from '@tanstack/react-query';
import { toUserMessage } from '@/api/errors';
import { getUserImages } from '@/api/userApi';
import { Button } from '@/components/common/Button';
import { EmptyState, ErrorState, LoadingState } from '@/components/common/StateViews';
import { SelectField } from '@/components/common/SelectField';
import { FormField } from '@/components/common/FormField';
import { DiscoverCard } from '@/components/dating/DiscoverCard';
import { MatchNotice, type LikeNotice } from '@/components/dating/MatchNotice';
import { getNearbyUsers, MAX_DISTANCE_KM, MIN_DISTANCE_KM } from '@/features/discover/discoverApi';
import { loadDistance, loadSeen, saveDistance, saveSeen } from '@/features/discover/discoverStorage';
import { buildQueue, hasActiveFilters, NO_FILTERS, type DiscoverFilters } from '@/features/discover/filters';
import type { LikeOutcome } from '@/features/likes/likeFlow';
import { useLikeUser } from '@/features/likes/useLikeUser';
import { approvedImages } from '@/features/media/images';
import { GENDER_OPTIONS, RELATIONSHIP_OPTIONS } from '@/features/profile/options';
import { useGeolocation } from '@/hooks/useGeolocation';
import { usePageTitle } from '@/hooks/usePageTitle';
import type { NearbyUser } from '@/types/social';
import { displayName, nextPageParam } from '@/utils/format';

const PREFETCH_WHEN_BELOW = 3;

function toNotice(outcome: LikeOutcome, user: NearbyUser): LikeNotice {
  const name = displayName(user);
  if (outcome.kind === 'matched') return { kind: 'matched', userId: user.id, name };
  if (outcome.kind === 'already-liked') return { kind: 'already-liked', name };
  return { kind: 'liked', name, matchCheckFailed: outcome.matchCheckFailed };
}

export function DiscoverPage() {
  usePageTitle('Discover');
  const location = useGeolocation();
  const { request: requestLocation } = location;
  const coords = location.coords;

  const [distanceKm, setDistanceKm] = useState(loadDistance);
  const [filters, setFilters] = useState<DiscoverFilters>(NO_FILTERS);
  const [seen, setSeen] = useState(loadSeen);
  const [notice, setNotice] = useState<LikeNotice | null>(null);
  const like = useLikeUser();

  // If the browser already has permission, use it without making the person click.
  useEffect(() => {
    navigator.permissions
      ?.query({ name: 'geolocation' })
      .then((status) => {
        if (status.state === 'granted') requestLocation();
      })
      .catch(() => undefined);
  }, [requestLocation]);

  const nearby = useInfiniteQuery({
    queryKey: ['nearby', coords?.latitude, coords?.longitude, distanceKm],
    queryFn: ({ pageParam, signal }) => {
      if (!coords) throw new Error('Location is required.');
      return getNearbyUsers({ latitude: coords.latitude, longitude: coords.longitude, distanceKm, page: pageParam }, signal);
    },
    initialPageParam: 0,
    getNextPageParam: nextPageParam,
    enabled: coords !== null,
  });

  const loaded = useMemo(() => nearby.data?.pages.flatMap((page) => page.content) ?? [], [nearby.data]);
  const queue = useMemo(() => buildQueue(loaded, seen, filters), [loaded, seen, filters]);
  const current = queue[0];

  // Keep a few profiles ready, and keep going when the filters hide everything loaded so far.
  const { hasNextPage, isFetchingNextPage, isError, fetchNextPage } = nearby;
  useEffect(() => {
    if (queue.length < PREFETCH_WHEN_BELOW && hasNextPage && !isFetchingNextPage && !isError) void fetchNextPage();
  }, [queue.length, hasNextPage, isFetchingNextPage, isError, fetchNextPage]);

  // Some mappers return photos with the nearby result, others do not; fetch them only when missing.
  const embedded = approvedImages(current?.images);
  const fetched = useQuery({
    queryKey: ['user-images', current?.id],
    queryFn: ({ signal }) => getUserImages(current?.id ?? 0, signal),
    enabled: current !== undefined && embedded.length === 0,
  });
  const images = embedded.length > 0 ? embedded : approvedImages(fetched.data);

  // Move focus to the new person's name so screen-reader users hear who is next.
  const firstRender = useRef(true);
  useEffect(() => {
    if (firstRender.current) {
      firstRender.current = false;
      return;
    }
    if (current) document.getElementById(`discover-${current.id}`)?.focus();
  }, [current?.id]); // eslint-disable-line react-hooks/exhaustive-deps -- only the identity of the card matters

  function markSeen(id: number) {
    setSeen((previous) => {
      const next = new Set(previous);
      next.add(id);
      saveSeen(next);
      return next;
    });
  }

  function onLike(user: NearbyUser) {
    setNotice(null);
    like.mutate(user.id, {
      onSuccess: (outcome) => {
        markSeen(user.id);
        setNotice(toNotice(outcome, user));
      },
      onError: (error) => setNotice({ kind: 'error', text: toUserMessage(error, "We couldn't send your like. Please try again.") }),
    });
  }

  function onPass(user: NearbyUser) {
    setNotice(null);
    markSeen(user.id);
  }

  return (
    <div className="container page">
      <h1 className="page__title">Discover</h1>

      <div className="discover-controls">
        <label className="discover-controls__distance">
          <span className="field__label">
            Distance: up to <output>{distanceKm} km</output>
          </span>
          <input
            type="range"
            min={MIN_DISTANCE_KM}
            max={MAX_DISTANCE_KM}
            step={1}
            value={distanceKm}
            onChange={(event) => {
              const value = Number(event.target.value);
              setDistanceKm(value);
              saveDistance(value);
            }}
          />
        </label>
        <details className="discover-controls__filters">
          <summary>
            Filters{hasActiveFilters(filters) ? ' (on)' : ''}
          </summary>
          <p className="field__hint">Filters apply to the people loaded so far.</p>
          <div className="form-grid">
            <FormField label="Youngest" type="number" inputMode="numeric" min={18} max={120} value={filters.minAge} onChange={(e) => setFilters({ ...filters, minAge: e.target.value })} />
            <FormField label="Oldest" type="number" inputMode="numeric" min={18} max={120} value={filters.maxAge} onChange={(e) => setFilters({ ...filters, maxAge: e.target.value })} />
            <SelectField label="Gender" placeholder="Anyone" options={GENDER_OPTIONS} value={filters.gender} onChange={(e) => setFilters({ ...filters, gender: e.target.value })} />
            <SelectField label="Looking for" placeholder="Anything" options={RELATIONSHIP_OPTIONS} value={filters.relationshipType} onChange={(e) => setFilters({ ...filters, relationshipType: e.target.value })} />
          </div>
          {hasActiveFilters(filters) && (
            <Button variant="ghost" onClick={() => setFilters(NO_FILTERS)}>
              Clear filters
            </Button>
          )}
        </details>
      </div>

      <MatchNotice notice={notice} />

      {coords === null ? (
        <EmptyState
          title="Share your location to see people nearby"
          action={
            location.status === 'unsupported' ? undefined : (
              <Button loading={location.status === 'requesting'} onClick={requestLocation}>
                Share my location
              </Button>
            )
          }
        >
          {location.status === 'denied'
            ? "Location access is blocked. Allow it for this site in your browser's settings, then try again."
            : location.status === 'unsupported'
              ? "Your browser doesn't support sharing your location."
              : location.status === 'unavailable' || location.status === 'timeout'
                ? "We couldn't work out where you are. Check that location services are on, then try again."
                : 'SoulM uses it only to find people within the distance you choose.'}
        </EmptyState>
      ) : nearby.isPending ? (
        <LoadingState label="Finding people near you" />
      ) : nearby.isError && !current ? (
        <ErrorState error={nearby.error} onRetry={() => void nearby.refetch()} fallback="We couldn't load people nearby." />
      ) : current ? (
        <DiscoverCard
          key={current.id}
          user={current}
          images={images}
          busy={like.isPending}
          onLike={() => onLike(current)}
          onPass={() => onPass(current)}
        />
      ) : nearby.isFetchingNextPage || nearby.hasNextPage ? (
        <LoadingState label="Looking for more people" />
      ) : (
        <EmptyState title="No one new nearby">
          {hasActiveFilters(filters)
            ? 'No one matches your filters. Try widening them, or increase the distance.'
            : 'You have seen everyone within this distance. Try increasing it, or check back later.'}
        </EmptyState>
      )}
    </div>
  );
}
