package cz.timetracker.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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
public class UserEntity {

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
    private String email;

    /**
     * User's display name.
     */
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column
    private LocalDateTime cratedAt;
}
