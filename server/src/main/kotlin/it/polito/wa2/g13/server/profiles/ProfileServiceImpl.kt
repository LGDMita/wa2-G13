package it.polito.wa2.g13.server.profiles

import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ProfileServiceImpl(
    private val profileRepository: ProfileRepository
) : ProfileService {
    override fun getProfile(username: String): ProfileDTO? {
        return profileRepository.findByIdOrNull(username)?.toDTO()
    }

    @Transactional
    override fun setProfile(profileDTO: ProfileDTO): Boolean{
        return if(!profileRepository.existsById(profileDTO.username)){
            profileRepository.save(profileDTO.toProfile())
            true
        }else{
            false
        }
    }

    @Transactional
    override fun modifyProfile(username: String, profileDTO: ProfileDTO) {
        profileRepository.save(profileDTO.toProfile())
    }

    @Transactional
    override fun deleteProfile(id: String) {
        profileRepository.deleteById(id)
    }
}