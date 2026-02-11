import { HttpRequestError } from "../errors/HttpRequestError";

const API_URL = "http://localhost:8080/api";
type ApiErrorBody = {
  message?: string;
  code?: number;
  details?: unknown;
};

/**
 * Performs a GET request to the given endpoint.
 *
 * @param {string} endpoint - The API endpoint to query.
 * @returns {Promise<T>} The JSON response from the API.
 * @throws {HttpRequestError} - If the request fails or the response is not OK.
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
    if (error instanceof Error) {
      throw new HttpRequestError(
        error.message,
        0, // Application code
        0, // Http status unknown
        `${API_URL}${endpoint}`,
      );
    }
    throw new HttpRequestError("Unknown API error");
  }

  if (!response.ok) {
    let errorBody: ApiErrorBody | null = null;
    try {
      errorBody = await response.json();
    } catch {
      // Silently ignore errors when parsing error body
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

export async function apiGetById<T>(endpoint: string, id: number): Promise<T> {
  return apiGet(`${endpoint}/${id}`);
}

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
    let errorBody: ApiErrorBody | null = null;

    try {
      errorBody = await response.json();
    } catch {
      throw new HttpRequestError(
        errorBody?.message ?? response.statusText,
        errorBody?.code ?? -1,
        response.status,
        errorBody?.details,
      );
    }
  }
  return response.json() as Promise<T>;
}

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
        `Network error při mazání ${endpoint}: ${error.message}`,
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
    throw new HttpRequestError(
      `Chyba při mazání ${endpoint}: ${response.status}: ${response.statusText}`,
      response.status,
    );
  }
}
