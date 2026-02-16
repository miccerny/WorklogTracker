package cz.timetracker.dto.user;


import jakarta.validation.constraints.Email;

public record UserRegistryRequest (

  String username,
  String name,
  String password
){}
