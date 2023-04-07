package it.polito.wa2.g13.server.profiles

import org.springframework.stereotype.Service
import org.springframework.validation.BindingResult

@Service
interface ProfileService {
    fun getProfile(email: String): ProfileDTO?

    fun setProfile(profileDTO: ProfileDTO, br: BindingResult): Boolean

    fun modifyProfile(email: String, profileDTO: ProfileDTO, br: BindingResult): Boolean
}