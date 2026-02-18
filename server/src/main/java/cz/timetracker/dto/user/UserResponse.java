package cz.timetracker.dto.user;


import java.time.LocalDateTime;

/**
 * DTO representing user data returned by the API.
 *
 * <p>This object is typically used after registration
 * or when returning authenticated user information.</p>
 *
 * <p><b>Note:</b>
 * This response does NOT contain sensitive information
 * such as the user's password. Only safe, public-facing
 * data should be exposed in response DTOs.</p>
 *
 * <p>This is implemented as a Java {@code record}, meaning:
 * <ul>
 *     <li>It is immutable</li>
 *     <li>It is thread-safe</li>
 *     <li>It is intended purely as a data carrier</li>
 * </ul>
 * </p>
 *
 * @param id unique identifier of the user
 * @param username user's login identifier (typically email)
 * @param name display/display name of the user
 * @param createdAt timestamp when the user account was created
 */
public record UserResponse (
    Long id,
    String username,
    String name,
    LocalDateTime createdAt
){}
