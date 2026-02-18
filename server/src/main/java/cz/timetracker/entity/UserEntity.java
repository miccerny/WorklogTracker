package cz.timetracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * JPA entity representing an application user.
 *
 * <p>This entity maps to the database table storing registered users.
 * A user can own multiple WorkLogs.</p>
 *
 * <p>This class implements {@link UserDetails}, which allows it
 * to be used directly by Spring Security during authentication.</p>
 *
 * <p><b>Note:</b>
 * This entity represents database structure only.
 * Authentication and authorization logic belong to the security layer.</p>
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class UserEntity implements UserDetails {

    /**
     * Primary key of the user.
     *
     * <p>Generated automatically using a database sequence.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(sequenceName = "user_seq", allocationSize = 1)
    private Long id;

    /**
     * Unique email address of the user.
     *
     * <p>Used as the login identifier.</p>
     *
     * <p><b>Note:</b>
     * Validation annotations such as {@link Email} and {@link NotBlank}
     * ensure basic input validation before persistence.</p>
     */
    @Column(nullable = false)
    @Email
    @NotBlank
    private String username;

    /**
     * User's display name.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Hashed password of the user.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Timestamp when the user account was created.
     */
    @Column
    private LocalDateTime createdAt;

    /**
     * One-to-Many relationship:
     * One user can own multiple WorkLogs.
     *
     * <p><b>Note:</b>
     * - {@code mappedBy = "owner"} means the relationship is managed
     *   on the WorkLogEntity side.
     * - {@code cascade = CascadeType.ALL} means related WorkLogs
     *   are affected by user operations (persist, delete, etc.).
     * - {@code orphanRemoval = true} removes WorkLogs that are no longer
     *   associated with the user.</p>
     */
    @OneToMany(mappedBy = "owner", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<WorkLogEntity> worklog;

    /**
     * Convenience constructor used during registration.
     *
     * @param name display name
     * @param username email/login identifier
     * @param encodedPassword already encoded (hashed) password
     */
    public UserEntity(String name, String username, String encodedPassword) {
        this.name = name;
        this.username = username;
        this.password = encodedPassword;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Returns granted authorities (roles/permissions).
     *
     * <p>Currently returns an empty list because
     * role-based authorization is not implemented.</p>
     *
     * <p><b>Note:</b>
     * If roles were added (e.g. ROLE_USER, ROLE_ADMIN),
     * they would be returned here.</p>
     *
     * @return collection of granted authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
}
