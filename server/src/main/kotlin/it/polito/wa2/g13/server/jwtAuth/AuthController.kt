package it.polito.wa2.g13.server.jwtAuth

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g13.server.profiles.InvalidArgumentsException
import it.polito.wa2.g13.server.profiles.ProfileService
import jakarta.validation.Valid
import lombok.extern.slf4j.Slf4j
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.*
import javax.ws.rs.core.Response


@RestController
@Observed
@Slf4j
class AuthController(
    private val authService: AuthService
) {

    private val log = KotlinLogging.logger {}

    @PostMapping("/API/login")
    fun login(
        @Valid @RequestBody loginDTO: LoginDTO
    ): JwtResponse {
        log.info("recieve create order command, order = {}.", loginDTO)
        return authService.login(loginDTO) ?: throw InvalidCredentialArgumentsException()
    }

    @PostMapping("/API/signup")
    fun registerUser(@RequestBody @Valid registerDTO: RegisterDTO, br: BindingResult): ResponseEntity<Any> {
        return if (!br.hasErrors()) {
            val register = authService.register(registerDTO)
            transformResponse(register!!)
        } else
            throw InvalidArgumentsException()
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