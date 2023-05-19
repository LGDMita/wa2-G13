package it.polito.wa2.g13.server.jwtAuth

import jakarta.validation.constraints.*

data class RegisterDTO (
    @NotBlank
    var username: String,
    @NotBlank
    var password: String,
    @NotBlank
    @Email
    var email: String,
    @NotBlank
    var name: String,
    @NotBlank
    var surname: String
)