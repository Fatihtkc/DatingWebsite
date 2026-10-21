import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { RouterProvider } from 'react-router-dom';
import { ApiError } from '@/api/errors';
import { AuthProvider } from '@/features/auth/AuthProvider';
import { router } from './router';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 30_000,
      retry: (failureCount, error) => {
        // 4xx responses will not succeed on retry; network and 5xx failures get two more tries.
        if (error instanceof ApiError && error.status !== null && error.status < 500) return false;
        return failureCount < 2;
      },
    },
    // Mutations are never retried automatically: a retry could duplicate a like, upload or report.
    mutations: { retry: false },
  },
});

export function AppProviders() {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <RouterProvider router={router} />
      </AuthProvider>
    </QueryClientProvider>
  );
}
