package it.polito.wa2.g13.server.profiles

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.validation.BindingResult

@Service
class ProfileServiceImpl(
    private val profileRepository: ProfileRepository
) : ProfileService {
    override fun getProfile(username: String): ProfileDTO? {
        return profileRepository.findByIdOrNull(username)?.toDTO()
    }

    override fun setProfile(profileDTO: ProfileDTO): Boolean{
        return if(!profileRepository.existsById(profileDTO.username)){
            profileRepository.save(profileDTO.toProfile())
            true
        }else{
            false
        }
    }

    override fun modifyProfile(username: String, profileDTO: ProfileDTO): Boolean {
        return if(profileRepository.existsById(username)){
            profileRepository.save(profileDTO.toProfile())
            true
        }else{
            false
        }
    }
}