import axios from 'axios';

export type ApiErrorKind = 'http' | 'network' | 'timeout' | 'canceled' | 'unknown';

export interface ApiErrorInit {
  kind: ApiErrorKind;
  message: string;
  status?: number | null;
  code?: string | null;
  path?: string | null;
  fieldErrors?: Record<string, string>;
  cause?: unknown;
}

/** The only error type the rest of the app deals with. Produced by the Axios interceptors. */
export class ApiError extends Error {
  readonly kind: ApiErrorKind;
  readonly status: number | null;
  readonly code: string | null;
  readonly path: string | null;
  /** DTO property name to message, from a 400 VALIDATION_FAILED response. Empty otherwise. */
  readonly fieldErrors: Readonly<Record<string, string>>;

  constructor(init: ApiErrorInit) {
    super(init.message, { cause: init.cause });
    this.name = 'ApiError';
    this.kind = init.kind;
    this.status = init.status ?? null;
    this.code = init.code ?? null;
    this.path = init.path ?? null;
    this.fieldErrors = init.fieldErrors ?? {};
  }
}

const DEFAULT_MESSAGE = 'Something went wrong. Please try again.';
const NETWORK_MESSAGE = "We couldn't reach SoulM. Check your connection and try again.";
const TIMEOUT_MESSAGE = 'That took too long. Please try again.';

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null;
}

function readFieldErrors(value: unknown): Record<string, string> {
  const result: Record<string, string> = {};
  if (!isRecord(value)) return result;
  for (const [key, message] of Object.entries(value)) {
    if (typeof message === 'string') result[key] = message;
  }
  return result;
}

function readErrorBody(data: unknown, status: number) {
  if (isRecord(data)) {
    if (typeof data.code === 'string' && typeof data.message === 'string') {
      return {
        code: data.code,
        message: data.message,
        path: typeof data.path === 'string' ? data.path : null,
        fieldErrors: readFieldErrors(data.fieldErrors),
      };
    }
    // RateLimitFilter replies with {"error": "..."} instead of the standard envelope.
    if (typeof data.error === 'string') {
      return { code: status === 429 ? 'RATE_LIMITED' : null, message: data.error, path: null, fieldErrors: {} };
    }
  }
  return { code: null, message: null, path: null, fieldErrors: {} };
}

export function normalizeApiError(error: unknown): ApiError {
  if (error instanceof ApiError) return error;

  if (axios.isCancel(error)) {
    return new ApiError({ kind: 'canceled', message: 'Request canceled.', cause: error });
  }

  if (axios.isAxiosError(error)) {
    if (error.response) {
      const { status, data } = error.response;
      const body = readErrorBody(data, status);
      return new ApiError({
        kind: 'http',
        status,
        code: body.code,
        path: body.path,
        fieldErrors: body.fieldErrors,
        message: body.message ?? `Request failed with status ${status}.`,
        cause: error,
      });
    }
    if (error.code === 'ECONNABORTED' || error.code === 'ETIMEDOUT') {
      return new ApiError({ kind: 'timeout', message: 'The request timed out.', cause: error });
    }
    return new ApiError({ kind: 'network', message: 'Network error.', cause: error });
  }

  return new ApiError({
    kind: 'unknown',
    message: error instanceof Error ? error.message : 'Unknown error.',
    cause: error,
  });
}

export function isCanceled(error: unknown): boolean {
  return normalizeApiError(error).kind === 'canceled';
}

/**
 * Text that is safe to show to a person. Server messages are passed through for 4xx
 * responses (the backend writes those for end users); 5xx and unknown failures never
 * surface technical detail.
 */
export function toUserMessage(error: unknown, fallback: string = DEFAULT_MESSAGE): string {
  const apiError = normalizeApiError(error);

  switch (apiError.kind) {
    case 'canceled':
      return '';
    case 'network':
      return NETWORK_MESSAGE;
    case 'timeout':
      return TIMEOUT_MESSAGE;
    case 'unknown':
      return fallback;
    case 'http':
      break;
  }

  const status = apiError.status ?? 0;
  if (status >= 500) return fallback;
  if (status === 401) return 'Please log in to continue.';
  if (status === 403) return "You don't have permission to do that.";
  return apiError.message || fallback;
}
