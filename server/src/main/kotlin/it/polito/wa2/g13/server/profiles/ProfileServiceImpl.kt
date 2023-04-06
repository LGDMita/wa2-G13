package it.polito.wa2.g13.server.profiles

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ProfileServiceImpl(
    private val profileRepository: ProfileRepository
) : ProfileService {
    override fun getProfile(email: String): ProfileDTO? {
        return profileRepository.findByIdOrNull(email)?.toDTO()
    }

    override fun setProfile(profile: Profile): Boolean{
        return if(!profileRepository.existsById(profile.email)){
            profileRepository.save(profile)
            true
        }else{
            false
        }
    }

    override fun modifyProfile(email: String, profile: Profile): Boolean {
        return if(profileRepository.existsById(email)){
            profileRepository.deleteById(email)
            profileRepository.save(profile)
            true
        }else{
            false
        }
    }
}