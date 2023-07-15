package it.polito.wa2.g13.server.purchase

import it.polito.wa2.g13.server.profiles.Profile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PurchaseRepository : JpaRepository<Purchase,Long>{
    fun findAllByProfile(profile: Profile) : List<Purchase>
}