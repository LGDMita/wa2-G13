package it.polito.wa2.g13.ticketing.tickets

import java.util.Date

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name= "tickets")
class Ticket(
    @Id
    var ticketId: Long,
    var profileId: Long,
    var ean: String,
    var priorityLevel: Int,
    var expertId: Long,
    var status: String,
    var creationDate: Date
) {

}

fun TicketDTO.toTicket(): Ticket {
    return Ticket(ticketId, profileId, ean, priorityLevel, expertId, status, creationDate)
}
