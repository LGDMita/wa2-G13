package it.polito.wa2.g13.server.profiles

import org.springframework.web.bind.annotation.*

@RestController
class ProfileController(
    private val profileService : ProfileService
) : ProfileService{

    @CrossOrigin(origins = ["http://localhost:3000"])
    @GetMapping("/profiles/{email}")
    override fun getProfile(@PathVariable email: String) : ProfileDTO?{
        return profileService.getProfile(email) ?: throw ProfileNotFoundException()
    }
    @CrossOrigin(origins = ["http://localhost:3000"])
    @PostMapping("/profiles/")
    override fun setProfile(@RequestBody profile: Profile) : Boolean{
        return if(profileService.setProfile(profile)) {
            true
        } else throw DuplicateProfileException()
    }
    @CrossOrigin(origins = ["http://localhost:3000"])
    @PutMapping("/profiles/{email}")
    override fun modifyProfile(@PathVariable email: String, @RequestBody profile: Profile) : Boolean{
        return if(profileService.modifyProfile(email, profile)){
            true
        }else throw ProfileNotFoundException()
    }
}