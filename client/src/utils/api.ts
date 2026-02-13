import { HttpRequestError } from "../errors/HttpRequestError";

/**
 * Base URL for the backend API.
 * Tip: in real deployments this is usually read from env variables (Vite: import.meta.env).
 */
const API_URL = "http://localhost:8080/api";

/**
 * Expected shape of an API error response body.
 * The backend might return:
 * - message: human readable message
 * - code: business/application error code
 * - details: any structured details (e.g. validation errors)
 */
type ApiErrorBody = {
  message?: string;
  code?: number;
  details?: unknown;
};

/**
 * Performs a GET request to the given endpoint.
 *
 * - Uses `credentials: "include"` so cookies/session are sent to backend.
 * - Throws HttpRequestError for:
 *   - network errors (fetch failed)
 *   - non-2xx HTTP status (response.ok === false)
 */
export async function apiGet<T>(endpoint: string): Promise<T> {
  let response: Response;

  try {
    response = await fetch(`${API_URL}${endpoint}`, {
      method: "GET",
      credentials: "include",
      headers: { "Content-Type": "application/json" },
    });
  } catch (error) {
    // Network-level error: DNS, connection refused, CORS failure, etc.
    if (error instanceof Error) {
      throw new HttpRequestError(
        error.message,
        0, // Application code (custom: 0 = network/unknown)
        0, // HTTP status is unknown here (fetch did not return response)
        `${API_URL}${endpoint}`, // Details can store URL for easier debugging
      );
    }
    // Fallback for non-Error throws (rare, but TypeScript allows unknown)
    throw new HttpRequestError("Unknown API error");
  }

  if (!response.ok) {
    /**
     * For non-OK HTTP responses we try to parse JSON body with details.
     * If parsing fails (empty body / invalid JSON), we keep errorBody = null.
     */
    let errorBody: ApiErrorBody | null = null;
    try {
      errorBody = await response.json();
    } catch {
      // Intentionally ignored - we still throw a meaningful error below
    }

    throw new HttpRequestError(
      errorBody?.message ?? response.statusText,
      errorBody?.code ?? -1,
      response.status,
      errorBody?.details,
    );
  }
  // Successful response: parse JSON into T.
  return response.json() as Promise<T>;
}

/**
 * Convenience helper for GET /resource/{id}.
 * Keeps callers clean: apiGetById("/worklogs", 10)
 */
export async function apiGetById<T>(endpoint: string, id: number): Promise<T> {
  return apiGet(`${endpoint}/${id}`);
}

/**
 * Performs a POST request with JSON body.
 *
 * Generic types:
 * - T = response type
 * - B = request body type
 */
export async function apiPost<T, B>(endpoint: string, body: B): Promise<T> {
  let response: Response;

  try {
    response = await fetch(`${API_URL}${endpoint}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      credentials: "include",
      body: JSON.stringify(body),
    });
  } catch (error) {
    // Network-level error
    if (error instanceof Error) {
     
      throw new HttpRequestError(
        error.message,
        0, // Application code
        0, // Http status unknown
        `${API_URL}${endpoint}`,
      );
    }
    throw new HttpRequestError("Unknown network error");
  }

  if (!response.ok) {
     /**
       * ⚠️ BUG in your current code:
       * You throw inside the `catch` block, but if JSON parsing succeeds,
       * you currently do NOT throw at all and you fall through to return response.json().
       *
       * Correct approach:
       * - try to parse errorBody
       * - regardless of parsing success, throw HttpRequestError
       */
    let errorBody: ApiErrorBody | null = null;

    try {
      errorBody = await response.json();
    } catch {
      // keep errorBody = null
    }
     throw new HttpRequestError(
        errorBody?.message ?? response.statusText,
        errorBody?.code ?? -1,
        response.status,
        errorBody?.details,
      );
  }
  return response.json() as Promise<T>;
}

/**
 * Performs a PUT request with JSON body.
 * Structure is identical to POST but method differs.
 */
export async function apiPut<T, B>(endpoint: string, body: B): Promise<T> {
  let response: Response;

  try {
    response = await fetch(`${API_URL}${endpoint}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      credentials: "include",
      body: JSON.stringify(body),
    });
  } catch (error) {
    if (error instanceof Error) {
      throw new HttpRequestError(
        error.message,
        0, // Application code
        0, // Http status unknown
        `${API_URL}${endpoint}`,
      );
    }
    throw new HttpRequestError("Unknown network error");
  }

  if (!response.ok) {
     /**
     * ⚠️ Same issue as POST: you want to throw even when JSON parsing succeeds.
     */
    let errorBody: ApiErrorBody | null = null;

    try {
      errorBody = await response.json();
    } catch {
       // keep errorBody = null
    }
    throw new HttpRequestError(
        errorBody?.message ?? response.statusText,
        errorBody?.code ?? -1,
        response.status,
        errorBody?.details,
      );
  }
  return response.json() as Promise<T>;
}

/**
 * Performs a DELETE request.
 *
 * - Returns void (no JSON body expected)
 * - Throws HttpRequestError on network errors and non-OK HTTP responses
 */
export async function apiDelete(endpoint: string): Promise<void> {
  let response: Response;

  try {
    response = await fetch(`${API_URL}${endpoint}`, {
      method: "DELETE",
      credentials: "include",
    });
  } catch (error) {
    if (error instanceof Error) {
      throw new HttpRequestError(
        `Unknown Network error při mazání ${endpoint}: ${error.message}`,
        0,
        0,
        `${API_URL}${endpoint}`,
      );
    }
    throw new HttpRequestError(
      `Unknown network error při mazání ${endpoint}`,
      0,
      0,
      `${API_URL}${endpoint}`,
    );
  }
  if (!response.ok) {
    /**
     * Note: Here you are not trying to parse an error body.
     * That is fine if your backend doesn't return JSON on DELETE errors.
     * If it does, you can reuse the same parsing pattern as in GET/POST/PUT.
     */
    throw new HttpRequestError(
      `Chyba při mazání ${endpoint}: ${response.status}: ${response.statusText}`,
      response.status,
    );
  }
}
