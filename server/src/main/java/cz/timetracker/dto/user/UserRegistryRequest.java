package cz.timetracker.dto.user;

/**
 * DTO representing a user registration request.
 *
 * <p>This object is used to transfer new user data
 * from the client to the backend during registration.</p>
 *
 * <p><b>Note:</b>
 * This is a Java {@code record}, which is a compact and immutable
 * data carrier. It automatically provides:
 * <ul>
 *     <li>constructor</li>
 *     <li>getter methods</li>
 *     <li>equals() and hashCode()</li>
 *     <li>toString()</li>
 * </ul>
 * </p>
 *
 * <p>The password provided here is a raw (plain text) password.
 * It must always be hashed using a {@code PasswordEncoder}
 * before being stored in the database.</p>
 *
 * @param username unique login identifier (typically email)
 * @param name display name of the user
 * @param password raw password provided during registration
 */
public record UserRegistryRequest (

  String username,
  String name,
  String password
){}
