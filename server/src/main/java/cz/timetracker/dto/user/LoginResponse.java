package cz.timetracker.dto.user;

/**
 * DTO representing a successful login response.
 *
 * <p>This object is returned to the client after successful authentication.
 * It contains basic user information and the generated JWT access token.</p>
 *
 * <p><b>Note:</b>
 * The {@code accessToken} is used by the frontend to authenticate
 * subsequent HTTP requests. It is typically sent in the
 * {@code Authorization: Bearer <token>} header.</p>
 *
 * <p>This is implemented as a Java {@code record}, meaning it is:
 * <ul>
 *     <li>immutable</li>
 *     <li>thread-safe</li>
 *     <li>a simple data carrier without business logic</li>
 * </ul>
 * </p>
 *
 * @param id unique identifier of the authenticated user
 * @param username user's login identifier (typically email)
 * @param name display name of the user
 * @param accessToken JWT token used for authenticating future requests
 */
public record LoginResponse(
        Long id,
        String username,
        String name,
        String accessToken
) {
}
