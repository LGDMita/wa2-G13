package it.polito.wa2.g13.server.ticketing.experts

import it.polito.wa2.g13.server.EntityBase
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name= "experts")
class Expert(
    @Id
    var expertId: Long,
    var name: String,
    var surname: String,
    var sector: String,
    var email: String,
    setId: Long?=null) : EntityBase<Long>(setId) {
}

fun ExpertDTO.toExpert(): Expert {
    return Expert(name, surname, sector, email,expertId)
}