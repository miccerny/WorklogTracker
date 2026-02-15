package cz.timetracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * JPA entity representing application user.
 *
 * <p>This entity maps to database table storing registered users.
 * A user can own multiple WorkLogs.
 *
 * <p>This class is responsible only for data structure.
 * Authentication logic should be handled elsewhere (e.g. Security layer).
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class UserEntity implements UserDetails {

    /**
     * Primary key of the user.
     * Generated automatically by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(sequenceName = "user_seq", allocationSize = 1)
    private Long id;

    /**
     * Unique email address of the user.
     * Used for login / identification.
     */
    @Column(nullable = false)
    @Email
    private String username;

    /**
     * User's display name.
     */
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column
    private LocalDateTime createdAt;

    public UserEntity(String username, String name, String encodedPassword) {
        this.username = username;
        this.name = name;
        this.password = encodedPassword;
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
}
