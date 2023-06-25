package it.polito.wa2.g13.server.profiles

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g13.server.jwtAuth.AuthService
import jakarta.transaction.Transactional
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
    private val profileService: ProfileService,
    private val authService: AuthService
) {

    private val log = LoggerFactory.getLogger(ProfileController::class.java)

    @GetMapping("/API/profiles/{id}")
    fun getProfile(@PathVariable  id: String): ProfileDTO? {
        log.info("Request profile with id: {}", id)
        return profileService.getProfile(id) ?: throw ProfileNotFoundException()
    }


    @Transactional
    @PutMapping("/API/profiles/{id}")
    fun modifyProfile(
        @PathVariable id: String,
        @RequestBody @Valid profileDTO: ProfileDTO,
        br: BindingResult
    ): Boolean {
        log.info("Edited profile with id: {} and profileDTO: {}", id, profileDTO.toString())
        return if (!br.hasErrors()) {
            val oldProfile=profileService.getProfile(id)?.toRegisterDTO()?:throw ProfileNotFoundException()
            authService.updateUser(id,oldProfile,profileDTO.toRegisterDTO())
            profileService.modifyProfile(id, profileDTO)
            true
        } else {
            log.info("Filed constraint not satisfied for DTO: {}", profileDTO.toString())
            throw InvalidArgumentsException()
        }
    }

    @Transactional
    @DeleteMapping("/API/profiles/{id}")
    fun deleteProfile(
        @PathVariable id: String
    ) {
        log.info("Deleting profile with id: {}", id)
        profileService.getProfile(id)?:throw ProfileNotFoundException()
        authService.deleteUser(id)
        profileService.deleteProfile(id)
    }
}