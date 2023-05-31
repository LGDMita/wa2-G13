package it.polito.wa2.g13.server.profiles

import org.springframework.stereotype.Service

@Service
interface ProfileService {
    fun getProfile(username: String): ProfileDTO?

    fun setProfile(profileDTO: ProfileDTO): Boolean

    fun modifyProfile(username: String, profileDTO: ProfileDTO): Unit

    fun saveNewProfile(profileDTO: ProfileDTO): Boolean

    fun deleteProfile(id: String): Unit
}