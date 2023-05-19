package it.polito.wa2.g13.server.jwtAuth

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@RestController
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/API/login")
    fun login(
        @Valid @RequestBody loginDTO: LoginDTO
    ): JwtResponse {
        return authService.login(loginDTO)
    }
}

data class JwtResponse(
    var jwtAccessToken: String
)