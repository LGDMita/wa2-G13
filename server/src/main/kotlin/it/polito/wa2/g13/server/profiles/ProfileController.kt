package it.polito.wa2.g13.server.profiles

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g13.server.jwtAuth.AuthController
import jakarta.validation.Valid
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@Validated
@Observed
@Slf4j
class ProfileController(
    private val profileService: ProfileService
) {

    private val log = LoggerFactory.getLogger(AuthController::class.java)

    @GetMapping("/API/profiles/{id}")
    fun getProfile(@PathVariable  id: String): ProfileDTO? {
        log.info("Request profile with id: {}", id)
        return profileService.getProfile(id) ?: throw ProfileNotFoundException()
    }

    //Must be modified or moved to pass the changes to keycloak as well
    @PutMapping("/API/profiles/{id}")
    fun modifyProfile(
        @PathVariable id: String,
        @RequestBody @Valid profileDTO: ProfileDTO,
        br: BindingResult
    ): Boolean {
        log.info("Edited profile with id: {} and profileDTO: {}", id, profileDTO.toString())
        return if (!br.hasErrors()) {
            if (profileService.modifyProfile(id, profileDTO)) true else {
                log.info("No profile found with id: {}", id)
                throw ProfileNotFoundException()
            }
        } else {
            log.info("Filed constraint not satisfied for DTO: {}", profileDTO.toString())
            throw InvalidArgumentsException()
        }
    }
}


//No longer necessary as subscription managed with keycloak
/*
@RestController
class ProfileControllerBR(
    private val profileService: ProfileService
) {
    @PostMapping("/API/profiles")
    fun setProfile(@RequestBody @Valid profileDTO: ProfileDTO, br: BindingResult): Boolean {
        if (!br.hasErrors()) {
            return if (profileService.setProfile(profileDTO)) true else throw DuplicateProfileException()
        }
        else
            throw InvalidArgumentsException()
    }
 }
 */