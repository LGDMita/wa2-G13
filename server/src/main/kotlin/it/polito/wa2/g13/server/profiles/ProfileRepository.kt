package it.polito.wa2.g13.server.profiles

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProfileRepository : JpaRepository<Profile, String> {
    fun findProfileByEmail(email: String): Profile?

    fun findProfileByUsername(username: String): Profile?
}