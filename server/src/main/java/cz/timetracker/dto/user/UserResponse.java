package cz.timetracker.dto.user;


import java.time.LocalDateTime;

public record UserResponse (
    Long id,
    String username,
    String name,
    LocalDateTime createdAt
){}
