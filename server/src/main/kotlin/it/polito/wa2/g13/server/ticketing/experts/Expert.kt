package it.polito.wa2.g13.server.ticketing.experts

import it.polito.wa2.g13.server.EntityBase
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name= "experts")
class Expert(
    var name: String,
    var surname: String,
    var email: String,
    setId: Long?=null) : EntityBase<Long>(setId) {

    @ManyToMany
    @JoinTable(name="expert_sector",
        joinColumns = [JoinColumn(name="expert_id")],
        inverseJoinColumns = [JoinColumn(name="sector_id")]
    )
    val sectors: MutableSet<Sector> = mutableSetOf()

    fun addSector(s: Sector) {
        sectors.add(s)
        s.experts.add(this)
    }

    fun removeSector(s: Sector){
        sectors.remove(s)
        s.experts.remove(this)
    }

}

fun ExpertDTO.toExpert(): Expert {
    return Expert(name, surname, email, expertId)
}

fun ExpertDTO.toExpertWithId(id: Long) : Expert{
    return Expert(name, surname, email, id)
}