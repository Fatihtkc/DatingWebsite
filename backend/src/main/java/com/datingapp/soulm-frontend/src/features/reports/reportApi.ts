import { http } from '@/api/axiosInstance';
import type { ComplaintCreateRequest } from '@/types/social';

const UPLOAD_TIMEOUT_MS = 120_000;

/**
 * Files a complaint about another member.
 *
 * BACKEND CHANGE REQUIRED: ComplaintController#createComplaint is mapped to POST "/api/complaints/"
 * with `consumes = multipart/form-data` but reads the DTO with @RequestBody. A multipart body cannot
 * be read as JSON, and the trailing slash does not match "/api/complaints" in Spring 6. This client
 * sends what the endpoint needs to look like after the fix: POST /api/complaints, multipart, a JSON
 * part named `data` and optional file parts named `images`
 * (`@RequestPart("data") @Valid ComplaintCreateDTO dto`).
 */
export async function createComplaint(request: ComplaintCreateRequest, evidence: readonly File[], signal?: AbortSignal): Promise<void> {
  const body = new FormData();
  body.append('data', new Blob([JSON.stringify(request)], { type: 'application/json' })); // without this type Spring answers 415
  for (const file of evidence) body.append('images', file);
  await http.post('/api/complaints', body, { signal, timeout: UPLOAD_TIMEOUT_MS });
}
