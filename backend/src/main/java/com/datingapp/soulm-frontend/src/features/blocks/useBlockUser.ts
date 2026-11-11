import { useMutation, useQueryClient } from '@tanstack/react-query';
import { blockUser } from './blocksApi';

/** Blocking also removes any match, so everything that lists people has to be refreshed. */
export function useBlockUser() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (userId: number) => blockUser(userId),
    onSuccess: (_data, userId) => {
      queryClient.removeQueries({ queryKey: ['user', userId] });
      for (const key of ['matches', 'likes', 'nearby', 'blocks', 'conversation', 'messages']) {
        void queryClient.invalidateQueries({ queryKey: [key] });
      }
    },
  });
}
