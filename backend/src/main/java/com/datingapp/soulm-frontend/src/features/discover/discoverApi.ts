import { http } from '@/api/axiosInstance';
import type { Page } from '@/types/api';
import type { NearbyUser } from '@/types/social';

export const MIN_DISTANCE_KM = 1;
export const MAX_DISTANCE_KM = 100; // the endpoint rejects anything above 100 (@DecimalMax)

export interface NearbyQuery {
  latitude: number;
  longitude: number;
  distanceKm: number;
  page: number;
  size?: number;
}

/**
 * GET /api/users/nearby. Already excludes the caller, unapproved and banned users and blocks in
 * either direction, ordered by distance. It does NOT exclude people the caller already liked, and
 * it has no age, gender or relationship filters (see docs/BACKEND_ANALYSIS.md).
 */
export async function getNearbyUsers(query: NearbyQuery, signal?: AbortSignal): Promise<Page<NearbyUser>> {
  const { data } = await http.get<Page<NearbyUser>>('/api/users/nearby', {
    params: { lat: query.latitude, lon: query.longitude, distanceKm: query.distanceKm, page: query.page, size: query.size ?? 20 },
    signal,
  });
  return data;
}
