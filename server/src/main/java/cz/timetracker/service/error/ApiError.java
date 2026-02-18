package cz.timetracker.service.error;

import java.time.LocalDateTime;

/**
 * Standard API error response returned by {@code @RestControllerAdvice}.
 *
 * <p><b>Beginner note:</b> A consistent error shape makes frontend handling simpler
 * (same fields for every error).</p>
 *
 * @param timestamp server time when the error happened
 * @param status HTTP status code (e.g. 404)
 * @param error HTTP status reason (e.g. "Not Found")
 * @param message human-readable error message
 * @param path request path that caused the error
 * @param code optional machine-readable code (e.g. "TIMER_ALREADY_RUNNING")
 */
public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        String code
) {
}
