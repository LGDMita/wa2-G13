package it.polito.wa2.g13.server.profiles

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ProfileController(
    private val profileService: ProfileService
) : ProfileService {

    @GetMapping("/API/profiles/{email}")
    override fun getProfile(@PathVariable email: String): ProfileDTO? {
        println("getProfile - email=${email}");
        return profileService.getProfile(email) ?: throw ProfileNotFoundException()
    }

    @PostMapping("/API/profiles/")
    override fun setProfile(
        @RequestBody email: String,
        @RequestBody name: String,
        @RequestBody surname: String
    ): Boolean {
        println("setProfile - email=${email} name=${name} surname=${surname}");
        return if (profileService.setProfile(email, name, surname)) {
            true
        } else throw DuplicateProfileException()
    }

    @PutMapping("/API/profiles/{email}")
    override fun modifyProfile(
        @PathVariable email: String,
        @RequestBody name: String,
        @RequestBody surname: String
    ): Boolean {
        println("modifyProfile - email=${email} name=${name} surname=${surname}");
        return if (profileService.modifyProfile(email, name, surname)) {
            true
        } else throw ProfileNotFoundException()
    }
}