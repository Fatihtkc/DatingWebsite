/**
 * Standard error envelope produced by GlobalExceptionHandler, CustomAuthenticationEntryPoint
 * and CustomAccessDeniedHandler. `timestamp` is a LocalDateTime serialised by Jackson.
 *
 * Note: RateLimitFilter answers 429 with a different body, `{ "error": string }`.
 * errors.ts normalises both shapes.
 */
export interface ApiErrorBody {
  timestamp: string;
  status: number;
  code: string;
  message: string;
  path: string;
  /**
   * Per-field messages, present on 400 VALIDATION_FAILED. Keys are DTO property names.
   * The property name `fieldErrors` is inferred from GlobalExceptionHandler (the ErrorResponse
   * record was not supplied); confirm it. Null on every other error.
   */
  fieldErrors: Record<string, string> | null;
}

/**
 * Subset of Spring Data's serialised `Page<T>` that the UI relies on.
 * Verify against a real response the first time a paginated endpoint is wired up.
 */
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  /** Zero-based page index. */
  number: number;
  size: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
  empty: boolean;
}

/** Query parameters accepted by every Pageable endpoint. The server caps `size` at 50 (default 20). */
export interface PageParams {
  page?: number;
  size?: number;
}
