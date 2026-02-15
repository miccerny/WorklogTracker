package cz.timetracker.dto.user;

public record LoginResponse(
        Long id,
        String username,
        String name
) {
}
