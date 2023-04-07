package it.polito.wa2.g13.server.profiles

import jakarta.validation.Valid
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@RestController
class ProfileController(
    private val profileService : ProfileService
) : ProfileService{

    @GetMapping("/API/profiles/{email}")
    override fun getProfile(@PathVariable email: String) : ProfileDTO?{
        //println("profile - getProfile email=${email}")
        return profileService.getProfile(email) ?: throw ProfileNotFoundException()
    }

    @PostMapping("/API/profiles")
    override fun setProfile(@RequestBody @Valid profileDTO: ProfileDTO, br: BindingResult) : Boolean{
        //println("profile - setProfile ${profile.toString()}")
        return if(!br.hasErrors()){
            if(profileService.setProfile(profileDTO, br)) true else throw DuplicateProfileException()
        }else throw InvalidArgumentsException()
    }

    @PutMapping("/API/profiles/{email}")
    override fun modifyProfile(@PathVariable email: String, @RequestBody @Valid profileDTO: ProfileDTO, br: BindingResult) : Boolean{
        //println("profile - modifyProfile email=${email}")
        return if(!br.hasErrors()) {
            if(profileService.modifyProfile(email, profileDTO, br)) true else throw ProfileNotFoundException()
        }else throw InvalidArgumentsException()
    }
}