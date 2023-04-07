package it.polito.wa2.g13.server.profiles

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.validation.BindingResult

@Service
class ProfileServiceImpl(
    private val profileRepository: ProfileRepository
) : ProfileService {
    override fun getProfile(email: String): ProfileDTO? {
        return profileRepository.findByIdOrNull(email)?.toDTO()
    }

    override fun setProfile(profileDTO: ProfileDTO,  br: BindingResult): Boolean{
        return if(!profileRepository.existsById(profileDTO.toProfile().email)){
            profileRepository.save(profileDTO.toProfile())
            true
        }else{
            false
        }
    }

    override fun modifyProfile(email: String, profileDTO: ProfileDTO, br: BindingResult): Boolean {
        return if(profileRepository.existsById(email)){
            profileRepository.deleteById(email)
            profileRepository.save(profileDTO.toProfile())
            true
        }else{
            false
        }
    }
}