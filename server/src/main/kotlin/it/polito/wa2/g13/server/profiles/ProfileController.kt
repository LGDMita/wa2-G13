package it.polito.wa2.g13.server.profiles

import it.polito.wa2.g13.server.jwtAuth.AuthService
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@Validated
class ProfileController(
    private val profileService: ProfileService,
    private val authService: AuthService
) {

    @GetMapping("/API/profiles/{id}")
    fun getProfile(@PathVariable  id: String): ProfileDTO? {
        return profileService.getProfile(id) ?: throw ProfileNotFoundException()
    }

    //Must be modified or moved to pass the changes to keycloak as well
    @Transactional
    @PutMapping("/API/profiles/{id}")
    fun modifyProfile(
        @PathVariable id: String,
        @RequestBody @Valid profileDTO: ProfileDTO,
        br: BindingResult
    ): Boolean {
        return if (!br.hasErrors()) {
            val oldProfile=profileService.getProfile(id)?.toRegisterDTO()?:throw ProfileNotFoundException()
            authService.updateUser(id,oldProfile,profileDTO.toRegisterDTO())
            profileService.modifyProfile(id, profileDTO)
            true
        } else throw InvalidArgumentsException()
    }
    @Transactional
    @DeleteMapping("/API/profiles/{id}")
    fun deleteProfile(
        @PathVariable id: String
    ): Unit {
        profileService.getProfile(id)?:throw ProfileNotFoundException()
        authService.deleteUser(id)
        profileService.deleteProfile(id)
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