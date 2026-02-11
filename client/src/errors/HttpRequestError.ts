export class HttpRequestError extends Error {
  status?;
  code?: number;
  details?: unknown;

  constructor(message?: string, code?: number, status?: number | string, details?: unknown){
    super(message)
    this.name = "HttpRequestError";
    this.code = code;
    this.status = status;
    this.details = details;
  }
} 