package it.polito.wa2.g13.server.jwtAuth

import jakarta.validation.constraints.*

data class LoginDTO (
    @NotBlank
    var username: String,
    @NotBlank
    var password: String
){
    override fun toString(): String {
        return "Username=${username}&Password=${password}"
    }
}