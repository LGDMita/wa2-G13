package it.polito.wa2.g13.server.jwtAuth

import it.polito.wa2.g13.server.profiles.DuplicateProfileException
import it.polito.wa2.g13.server.profiles.InvalidArgumentsException
import it.polito.wa2.g13.server.profiles.ProfileDTO
import it.polito.wa2.g13.server.profiles.ProfileService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.*
import javax.ws.rs.core.Response


@RestController
class AuthController(
    private val authService: AuthService,
    private val profileService: ProfileService
) {

    @PostMapping("/API/login")
    fun login(
        @Valid @RequestBody loginDTO: LoginDTO
    ): JwtResponse {
        return authService.login(loginDTO)?: throw InvalidCredentialArgumentsException()
    }

    @PostMapping("/API/signup")
    fun registerUser(@RequestBody @Valid registerDTO: RegisterDTO, br: BindingResult): ResponseEntity<Any> {

        return if (!br.hasErrors()){
            val register= authService.register(registerDTO)
            if (register == null)
                throw DuplicateProfileException()
            else{
                profileService.setProfile(ProfileDTO(registerDTO.username, registerDTO.email, registerDTO.name, registerDTO.surname))
                transformResponse(register)
            }
        }else
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