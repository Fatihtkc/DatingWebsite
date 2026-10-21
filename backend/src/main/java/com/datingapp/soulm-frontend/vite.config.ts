import { fileURLToPath, URL } from 'node:url';
import react from '@vitejs/plugin-react';
import { defineConfig } from 'vitest/config';

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: { '@': fileURLToPath(new URL('./src', import.meta.url)) },
  },
  // The backend's CORS config only allows http://localhost:3000 (app.cors.allowed-origins),
  // so the dev server must own that exact port. Open the app via "localhost", not 127.0.0.1.
  server: { port: 3000, strictPort: true },
  test: {
    environment: 'jsdom',
    setupFiles: ['./vitest.setup.ts'],
    css: false,
  },
});
