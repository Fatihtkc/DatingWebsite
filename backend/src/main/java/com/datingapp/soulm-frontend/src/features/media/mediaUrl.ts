import { http } from '@/api/axiosInstance';
import { env } from '@/config/env';

/** User and complaint images are stored as /uploads/<file>; staff images as absolute URLs. */
export function resolveMediaUrl(url: string): string {
  if (/^https?:\/\//i.test(url)) return url;
  return `${env.apiBaseUrl}${url.startsWith('/') ? '' : '/'}${url}`;
}

const blobUrls = new Map<string, Promise<string>>();

/**
 * Fetches an image with the bearer token and returns an object URL. Only for the API's own origin,
 * so the token is never sent anywhere else. Needed if /uploads/** requires authentication, because
 * an <img> tag cannot send an Authorization header (docs/BACKEND_ANALYSIS.md, B6).
 */
export function fetchImageAsBlobUrl(absoluteUrl: string): Promise<string> {
  if (!absoluteUrl.startsWith(env.apiBaseUrl)) return Promise.reject(new Error('Not an API URL.'));
  let cached = blobUrls.get(absoluteUrl);
  if (!cached) {
    cached = http
      .get<Blob>(absoluteUrl, { responseType: 'blob' })
      .then((response) => URL.createObjectURL(response.data))
      .catch((error: unknown) => {
        blobUrls.delete(absoluteUrl); // allow a later retry
        throw error;
      });
    blobUrls.set(absoluteUrl, cached);
  }
  return cached;
}
