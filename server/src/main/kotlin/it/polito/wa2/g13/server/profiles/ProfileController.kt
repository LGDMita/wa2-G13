package it.polito.wa2.g13.server.profiles

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class ProfileController(
    private val profileService : ProfileService
) : ProfileService{

    @GetMapping("/profiles/{email}")
    override fun getProfile(@PathVariable email: String) : ProfileDTO?{
        return profileService.getProfile(email) ?: throw ProfileNotFoundException()
    }
}