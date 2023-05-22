package it.polito.wa2.g13.server.ticketing.experts

import it.polito.wa2.g13.server.ticketing.sectors.Sector
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ExpertRepository : JpaRepository<Expert, String> {

    fun existsByEmailAndIdNot(email: String, id: String): Boolean

    fun existsByEmail(email: String): Boolean

    fun findExpertsBySectors(sectors: Sector): List<Expert>

}