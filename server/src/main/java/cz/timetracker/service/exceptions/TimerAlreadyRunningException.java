package cz.timetracker.service.exceptions;

/**
 * Thrown when a user tries to start a timer but another timer is already running.
 */
public class TimerAlreadyRunningException extends RuntimeException {
    public TimerAlreadyRunningException(String message) {
        super(message);
    }
}
