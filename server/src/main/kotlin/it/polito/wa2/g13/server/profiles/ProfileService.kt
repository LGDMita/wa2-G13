package it.polito.wa2.g13.server.profiles

import org.springframework.stereotype.Service

@Service
interface ProfileService {
    fun getProfile(id: String): ProfileDTO?

    fun setProfile(profileDTO: ProfileDTO): Boolean

    fun modifyProfile(id: String, profileDTO: ProfileDTO)

    fun deleteProfile(id: String)
}