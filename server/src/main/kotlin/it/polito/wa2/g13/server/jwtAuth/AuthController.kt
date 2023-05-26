package it.polito.wa2.g13.server.jwtAuth

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g13.server.profiles.DuplicateProfileException
import it.polito.wa2.g13.server.profiles.InvalidArgumentsException
import it.polito.wa2.g13.server.profiles.ProfileDTO
import it.polito.wa2.g13.server.profiles.ProfileService
import it.polito.wa2.g13.server.ticketing.experts.ExpertDTO
import it.polito.wa2.g13.server.ticketing.experts.ExpertService
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
import javax.ws.rs.core.Response.status


@RestController
@Observed
@Slf4j
class AuthController(
    private val authService: AuthService,
    private val profileService: ProfileService,
    private val expertService: ExpertService
) {

    private val log = KotlinLogging.logger {}

    @PostMapping("/API/login")
    fun login(
        @Valid @RequestBody loginDTO: LoginDTO
    ): JwtResponse {
        log.info("receive create order command, order = {}.", loginDTO)
        return authService.login(loginDTO) ?: throw InvalidCredentialArgumentsException()
    }

    @PostMapping("/API/signup")
    fun registerUser(@RequestBody @Valid registerDTO: RegisterDTO, br: BindingResult): ResponseEntity<Any> {
        return if (!br.hasErrors()) {
            val id= authService.register(registerDTO)
            if (id == null)
                throw DuplicateProfileException()
            else{
                profileService.setProfile(ProfileDTO(id, registerDTO.username, registerDTO.email, registerDTO.name, registerDTO.surname))
                transformResponse(status(Response.Status.CREATED)
                    .entity("Customer successfully created")
                    .build())
            }
        } else
            throw InvalidArgumentsException()
    }

    @PostMapping("/API/createExpert")
    fun createExpert(@RequestBody @Valid registerDTO: RegisterDTO, br: BindingResult): ResponseEntity<Any> {
        return if (!br.hasErrors()) {
            val id= authService.createExpert(registerDTO)
            if (id == null)
                throw DuplicateProfileException()
            else{
                expertService.setExpert(ExpertDTO(id, registerDTO.username, registerDTO.email, registerDTO.name, registerDTO.surname))
                transformResponse(status(Response.Status.CREATED)
                    .entity("Expert successfully created")
                    .build())
            }
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