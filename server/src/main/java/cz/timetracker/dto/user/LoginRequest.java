package cz.timetracker.dto.user;

/**
 * DTO representing a login request.
 *
 * <p>This object is used to transfer user credentials
 * (username and password) from the client to the backend.</p>
 *
 * <p><b>Note:</b>
 * This is implemented as a Java {@code record}, which is a concise way
 * to define an immutable data carrier. Records automatically generate:
 * <ul>
 *     <li>constructor</li>
 *     <li>getters</li>
 *     <li>equals() and hashCode()</li>
 *     <li>toString()</li>
 * </ul>
 * </p>
 *
 * @param username user's login identifier (typically email)
 * @param password user's raw password (will be validated by Spring Security)
 */
public record LoginRequest(
        String username,
        String password
) {
}
