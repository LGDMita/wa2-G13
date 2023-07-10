package it.polito.wa2.g13.server.ticketing.sectors

import it.polito.wa2.g13.server.ticketing.experts.Expert
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SectorRepository  : JpaRepository<Sector, Long> {

    fun findSectorsByExperts(expert: Expert) : List<Sector>

    fun existsByName(name: String) : Boolean

    fun findByName(name: String) : Sector

}