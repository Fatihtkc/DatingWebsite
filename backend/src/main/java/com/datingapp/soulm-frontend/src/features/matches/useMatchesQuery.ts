import { useInfiniteQuery } from '@tanstack/react-query';
import { nextPageParam } from '@/utils/format';
import { listMatches } from './matchesApi';

/** Shared by the Matches page and the Messages list so they use one cache entry. */
export function useMatchesQuery() {
  return useInfiniteQuery({
    queryKey: ['matches'],
    queryFn: ({ pageParam, signal }) => listMatches({ page: pageParam, size: 20 }, signal),
    initialPageParam: 0,
    getNextPageParam: nextPageParam,
  });
}
