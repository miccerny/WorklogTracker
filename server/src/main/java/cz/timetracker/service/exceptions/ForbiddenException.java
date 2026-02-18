package cz.timetracker.service.exceptions;

/**
 * Thrown when the user is authenticated but not allowed to access the resource.
 *
 * <p>Example: user tries to access a WorkLog owned by someone else.</p>
 */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
