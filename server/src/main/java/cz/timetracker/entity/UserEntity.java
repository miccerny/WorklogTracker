package cz.timetracker.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(sequenceName = "user_seq", allocationSize = 1)
    private Long id;

<<<<<<< Updated upstream
=======
    /**
     * User's display name.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Unique email address of the user.
     *
     * <p>Used as the login identifier.</p>
     *
     * <p><b>Note:</b>
     * Validation annotations such as {@link Email} and {@link NotBlank}
     * ensure basic input validation before persistence.</p>
     */
>>>>>>> Stashed changes
    @Column(nullable = false)
    private String email;

<<<<<<< Updated upstream
=======
    /**
     * Hashed password of the user.
     */
>>>>>>> Stashed changes
    @Column(nullable = false)
    private String password;

    @Column
    private LocalDateTime cratedAt;
}
