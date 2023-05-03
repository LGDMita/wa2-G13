package it.polito.wa2.g13.server.ticketing.sectors

import it.polito.wa2.g13.server.ticketing.experts.Expert
import jakarta.persistence.*

@Entity
@Table(name= "sectors")
class Sector(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
        generator = "sector_generator")
    @SequenceGenerator(name="sector_generator",
        sequenceName = "sector_seq",
        initialValue = 1,
        allocationSize = 1)
    var sectorId: Long,
    var name: String,
){
    @ManyToMany(mappedBy = "sectors")
    val experts: MutableSet<Expert> = mutableSetOf()
    fun addExpert(e: Expert) {
        experts.add(e)
        e.sectors.add(this)
    }

    fun removeExpert(e: Expert){
        experts.remove(e)
        e.sectors.remove(this)
    }
}

fun SectorDTO.toSector(): Sector {
    return Sector(sectorId, name)
}

