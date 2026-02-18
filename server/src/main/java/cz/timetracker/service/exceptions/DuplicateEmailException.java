package cz.timetracker.service.exceptions;

/**
 * Thrown when a user tries to register with an email/username that already exists.
 */
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}
