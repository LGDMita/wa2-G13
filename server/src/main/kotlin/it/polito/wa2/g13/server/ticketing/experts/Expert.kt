package it.polito.wa2.g13.server.ticketing.experts

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
    var email: String) {

}

fun ExpertDTO.toExpert(): Expert {
    return Expert(expertId, name, surname, sector, email)
}