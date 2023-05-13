package it.polito.wa2.g13.server.jwtAuth

import jakarta.validation.constraints.*

data class LoginDTO (
    @NotBlank
    var username: String,
    @NotBlank
    var password: String
)