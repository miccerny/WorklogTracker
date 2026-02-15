package cz.timetracker.dto.user;

public record LoginRequest(
        String username,
        String password
) {
}
