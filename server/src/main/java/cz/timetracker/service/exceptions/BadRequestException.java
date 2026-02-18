package cz.timetracker.service.exceptions;

/**
 * Thrown when the client sends invalid input (bad request).
 *
 * <p>Example: missing required fields, invalid parameters, etc.</p>
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
