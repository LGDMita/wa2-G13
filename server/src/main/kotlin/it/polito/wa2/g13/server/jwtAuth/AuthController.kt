package it.polito.wa2.g13.server.jwtAuth

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.*
import javax.ws.rs.core.Response


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

    @PostMapping("/API/signup")
    fun registerUser(@RequestBody registerDTO: RegisterDTO): ResponseEntity<Any> {
        return transformResponse(authService.register(registerDTO))
    }

    fun transformResponse(response: Response): ResponseEntity<Any> {
        val status = HttpStatus.valueOf(response.status)

        // Extract the response body from the original response
        val responseBody = response.entity

        // Construct the ResponseEntity using the extracted information
        return ResponseEntity.status(status).body(responseBody)
    }
}

data class JwtResponse(
    var jwtAccessToken: String
)