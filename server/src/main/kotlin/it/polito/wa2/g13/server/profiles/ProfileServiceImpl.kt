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

    override fun setProfile(email: String, name: String, surname: String): Boolean{
        return if(!profileRepository.existsById(email)){
            profileRepository.save(Profile(email, name, surname))
            true
        }else{
            false
        }
    }

    override fun modifyProfile(email: String,name: String, surname: String): Boolean {
        return if(profileRepository.existsById(email)){
            profileRepository.deleteById(email) // maybe not necessary, we have to control if the replacement in the DB is automatic or not
            profileRepository.save(Profile(email, name, surname))
            true
        }else{
            false
        }
    }
}