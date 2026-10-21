function readApiBaseUrl(): string {
  const value = import.meta.env.VITE_API_BASE_URL;
  if (!value) {
    if (import.meta.env.PROD) {
      throw new Error('VITE_API_BASE_URL is not set. Copy .env.example to .env.local and fill it in.');
    }
    // Spring Boot's default port; the supplied application.properties does not override server.port.
    return 'http://localhost:8080';
  }
  return value.replace(/\/+$/, '');
}

export const env = {
  apiBaseUrl: readApiBaseUrl(),
} as const;
