package it.polito.wa2.g13.server.ticketing.sectors

import it.polito.wa2.g13.server.EntityBase
import it.polito.wa2.g13.server.ticketing.experts.Expert
import jakarta.persistence.*

@Entity
@Table(name = "sectors")
class Sector(
    var name: String,
    setId: Long? = null
) : EntityBase<Long>(setId) {
    @ManyToMany(mappedBy = "sectors")
    val experts: MutableSet<Expert> = mutableSetOf()
    fun addExpert(e: Expert) {
        experts.add(e)
        e.sectors.add(this)
    }

    fun removeExpert(e: Expert) {
        experts.remove(e)
        e.sectors.remove(this)
    }
}

fun SectorDTO.toSector(): Sector {
    return Sector(name, sectorId)
}

