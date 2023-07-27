package it.polito.wa2.g13.server.purchase

import it.polito.wa2.g13.server.profiles.Profile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface PurchaseRepository : JpaRepository<Purchase, Long> {
    fun findAllByProfile(profile: Profile): List<Purchase>


    @Modifying
    @Query("UPDATE Purchase p SET p.profile = null WHERE p.profile = :profile")
    fun clearProfileIdForPurchaseWithProfiles(@Param("profile") profile: Profile?)
}