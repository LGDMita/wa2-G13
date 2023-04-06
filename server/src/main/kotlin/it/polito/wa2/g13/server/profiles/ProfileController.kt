package it.polito.wa2.g13.server.profiles

import org.springframework.web.bind.annotation.*

@RestController
class ProfileController(
    private val profileService : ProfileService
) : ProfileService{

    @GetMapping("/API/profiles/{email}")
    override fun getProfile(@PathVariable email: String) : ProfileDTO?{
        println("profile - getProfile email=${email}")
        return profileService.getProfile(email) ?: throw ProfileNotFoundException()
    }

    @PostMapping("/API/profiles")
    override fun setProfile(@RequestBody profile: Profile) : Boolean{
        println("profile - setProfile ${profile.toString()}")
        return if(profileService.setProfile(profile)) {
            true
        } else throw DuplicateProfileException()
    }

    @PutMapping("/API/profiles/{email}")
    override fun modifyProfile(@PathVariable email: String, @RequestBody profile: Profile) : Boolean{
        println("profile - modifyProfile email=${email}")
        return if(profileService.modifyProfile(email, profile)){
            true
        }else throw ProfileNotFoundException()
    }
}