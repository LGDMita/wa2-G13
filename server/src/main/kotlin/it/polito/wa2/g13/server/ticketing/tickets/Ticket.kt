package it.polito.wa2.g13.server.ticketing.tickets


import it.polito.wa2.g13.server.profiles.Profile
import it.polito.wa2.g13.server.ticketing.experts.Expert
import java.util.Date

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name= "tickets")
class Ticket(
    @Id
    var ticketId: Long,
    @ManyToOne
    var profile: Profile,
    var ean: String,
    var priorityLevel: Int,
    @ManyToOne
    var expert: Expert,
    var status: String,
    var creationDate: Date
) {

}

fun TicketDTO.toTicket(): Ticket {
    return Ticket(ticketId, profile, ean, priorityLevel, expert, status, creationDate)
}
