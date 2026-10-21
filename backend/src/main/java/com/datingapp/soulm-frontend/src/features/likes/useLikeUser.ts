import { useMutation, useQueryClient } from '@tanstack/react-query';
import { likeAndCheckMatch } from './likeFlow';

export function useLikeUser() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (userId: number) => likeAndCheckMatch(userId),
    onSuccess: (outcome) => {
      void queryClient.invalidateQueries({ queryKey: ['likes'] });
      if (outcome.kind === 'matched') void queryClient.invalidateQueries({ queryKey: ['matches'] });
    },
  });
}
