/**
 * Custom error type representing HTTP request failures.
 *
 * This class extends the native Error object and adds
 * additional metadata typically returned from backend APIs,
 * such as HTTP status, error code, and detailed payload.
 *
 * Purpose:
 * - Standardize error handling across the application
 * - Provide structured error data for UI notifications
 * - Allow type-safe error checks (instanceof HttpRequestError)
 */
export class HttpRequestError extends Error {

  /**
   * Optional HTTP status (e.g., 400, 404, 500).
   * Can be number or string depending on backend implementation.
   */
  status?;

   /**
   * Optional backend-specific error code.
   * Useful when API returns business error identifiers.
   */
  code?: number;

  /**
   * Additional backend error details (validation errors, payload, etc.).
   */
  details?: unknown;

  constructor(
    message?: string,
    code?: number,
    status?: number | string,
    details?: unknown
  ){
    super(message)

     // Set a custom error name for easier debugging
    this.name = "HttpRequestError";
    this.code = code;
    this.status = status;
    this.details = details;
  }
} 