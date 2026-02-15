package cz.timetracker.dto.user;


public record UserRegistryRequest (

  String username,
  String name,
  String password
){}
