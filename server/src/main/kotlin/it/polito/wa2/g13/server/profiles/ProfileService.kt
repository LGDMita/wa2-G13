package it.polito.wa2.g13.server.profiles

import org.springframework.stereotype.Service

@Service
interface ProfileService {
    fun getProfile(email: String): ProfileDTO?

    fun setProfile(profile: Profile): Boolean

    fun modifyProfile(email: String, profile: Profile): Boolean
}