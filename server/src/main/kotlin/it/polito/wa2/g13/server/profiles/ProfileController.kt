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

    @GetMapping("/API/profiles/{username}")
    fun getProfile(@PathVariable  username: String): ProfileDTO? {
        return profileService.getProfile(username) ?: throw ProfileNotFoundException()
    }

    @PutMapping("/API/profiles/{username}")
    fun modifyProfile(
        @PathVariable username: String,
        @RequestBody @Valid profileDTO: ProfileDTO,
        br: BindingResult
    ): Boolean {
        return if (!br.hasErrors()) {
            if (profileService.modifyProfile(username, profileDTO)) true else throw ProfileNotFoundException()
        } else throw InvalidArgumentsException()
    }
}

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