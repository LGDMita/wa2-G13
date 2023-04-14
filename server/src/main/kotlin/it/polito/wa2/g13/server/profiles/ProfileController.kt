package it.polito.wa2.g13.server.profiles

import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@Validated
class ProfileController(
    private val profileService: ProfileService
) {

    @GetMapping("/API/profiles/{email}")
    fun getProfile(@PathVariable @Email email: String): ProfileDTO? {
        return profileService.getProfile(email) ?: throw ProfileNotFoundException()
    }

    @PutMapping("/API/profiles/{email}")
    fun modifyProfile(
        @PathVariable @Email email: String,
        @RequestBody @Valid profileDTO: ProfileDTO,
        br: BindingResult
    ): Boolean {
        return if (!br.hasErrors()) {
            if (profileService.modifyProfile(email, profileDTO, br)) true else throw ProfileNotFoundException()
        } else throw InvalidArgumentsException()
    }
}

@RestController
class ProfileControllerBR(
    private val profileService: ProfileService
) {
    @PostMapping("/API/profiles")
    fun setProfile(@RequestBody @Valid profileDTO: ProfileDTO, br: BindingResult): Boolean {
        if (!br.hasErrors()) {
            return if (profileService.setProfile(profileDTO, br)) true else throw DuplicateProfileException()
        }
        else
            throw InvalidArgumentsException()
    }
}