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
    throw new HttpRequestError(
      "Network Error",
      0, // Application code
      0, // Http status unknown
      `${API_URL}${endpoint}`,
    );
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
      errorBody?.details
    );
  }

  return response.json() as Promise<T>;

}
