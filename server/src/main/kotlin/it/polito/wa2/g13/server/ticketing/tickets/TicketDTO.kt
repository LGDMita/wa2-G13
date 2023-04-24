package it.polito.wa2.g13.server.ticketing.tickets

import it.polito.wa2.g13.server.profiles.Profile
import it.polito.wa2.g13.server.ticketing.experts.Expert

import java.util.*

data class TicketDTO(
    var ticketId: Long,
    var profile: Profile,
    var ean: String,
    var priorityLevel: Int,
    var expert: Expert,
    var status: String,
    var creationDate: Date
)

fun Ticket.toDTO(): TicketDTO {
    return TicketDTO(ticketId, profile, ean, priorityLevel, expert, status, creationDate)
}