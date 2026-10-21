import axios, { AxiosError, AxiosHeaders, type AxiosResponse, type InternalAxiosRequestConfig } from 'axios';
import { describe, expect, it } from 'vitest';
import { ApiError, isCanceled, normalizeApiError, toUserMessage } from './errors';

function httpError(status: number, data: unknown): AxiosError {
  const config = { headers: new AxiosHeaders() } as InternalAxiosRequestConfig;
  const response = { status, statusText: '', data, headers: {}, config } as AxiosResponse;
  return new AxiosError('failed', AxiosError.ERR_BAD_RESPONSE, config, null, response);
}

describe('normalizeApiError', () => {
  it('reads the standard backend envelope', () => {
    const error = normalizeApiError(
      httpError(409, {
        timestamp: '2026-01-01T00:00:00',
        status: 409,
        code: 'DUPLICATE_RESOURCE',
        message: 'Email is already in use',
        path: '/api/users/check-user',
      }),
    );
    expect(error).toBeInstanceOf(ApiError);
    expect(error.kind).toBe('http');
    expect(error.status).toBe(409);
    expect(error.code).toBe('DUPLICATE_RESOURCE');
    expect(error.message).toBe('Email is already in use');
    expect(error.path).toBe('/api/users/check-user');
  });

  it('reads the rate-limit filter body, which has no code', () => {
    const error = normalizeApiError(httpError(429, { error: 'Too many requests. Please try again later.' }));
    expect(error.status).toBe(429);
    expect(error.code).toBe('RATE_LIMITED');
    expect(error.message).toBe('Too many requests. Please try again later.');
  });

  it('reads per-field validation messages from a 400 VALIDATION_FAILED', () => {
    const error = normalizeApiError(
      httpError(400, {
        timestamp: 't',
        status: 400,
        code: 'VALIDATION_FAILED',
        message: 'One or more fields are invalid.',
        path: '/api/users/signup',
        fieldErrors: { password: 'The password must contain at least one uppercase letter', username: 'size must be between 3 and 30', bad: 42 },
      }),
    );
    expect(error.code).toBe('VALIDATION_FAILED');
    expect(error.fieldErrors).toEqual({
      password: 'The password must contain at least one uppercase letter',
      username: 'size must be between 3 and 30',
    });
  });

  it('has no field errors when the body carries none', () => {
    expect(normalizeApiError(httpError(404, { timestamp: 't', status: 404, code: 'USER_NOT_FOUND', message: 'x', path: '/p', fieldErrors: null })).fieldErrors).toEqual({});
  });

  it('survives a non-JSON error body', () => {
    const error = normalizeApiError(httpError(502, '<html>Bad gateway</html>'));
    expect(error.kind).toBe('http');
    expect(error.code).toBeNull();
    expect(error.message).toBe('Request failed with status 502.');
  });

  it('classifies network failures, timeouts and cancellations', () => {
    expect(normalizeApiError(new AxiosError('Network Error', AxiosError.ERR_NETWORK)).kind).toBe('network');
    expect(normalizeApiError(new AxiosError('timeout', AxiosError.ECONNABORTED)).kind).toBe('timeout');
    expect(isCanceled(new axios.CanceledError())).toBe(true);
  });

  it('returns existing ApiError instances untouched', () => {
    const original = new ApiError({ kind: 'http', status: 400, message: 'x' });
    expect(normalizeApiError(original)).toBe(original);
  });
});

describe('toUserMessage', () => {
  it('passes through 4xx messages written for end users', () => {
    const error = httpError(429, {
      timestamp: 't',
      status: 429,
      code: 'TOO_MANY_LOGIN_ATTEMPTS',
      message: 'Too many failed attempts. Please try again later.',
      path: '/api/auth/login',
    });
    expect(toUserMessage(error)).toBe('Too many failed attempts. Please try again later.');
  });

  it('never shows server detail for 5xx responses', () => {
    const error = httpError(500, { timestamp: 't', status: 500, code: 'INTERNAL_SERVER_ERROR', message: 'NullPointerException at Foo', path: '/x' });
    expect(toUserMessage(error)).toBe('Something went wrong. Please try again.');
    expect(toUserMessage(error, 'Could not send the link.')).toBe('Could not send the link.');
  });

  it('gives connection and timeout problems their own wording', () => {
    expect(toUserMessage(new AxiosError('Network Error', AxiosError.ERR_NETWORK))).toMatch(/connection/i);
    expect(toUserMessage(new AxiosError('timeout', AxiosError.ECONNABORTED))).toMatch(/too long/i);
  });

  it('is empty for canceled requests so callers can ignore them', () => {
    expect(toUserMessage(new axios.CanceledError())).toBe('');
  });
});
