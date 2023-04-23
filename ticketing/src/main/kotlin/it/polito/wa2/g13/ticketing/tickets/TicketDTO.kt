package it.polito.wa2.g13.ticketing.tickets

import java.util.*

data class TicketDTO(
    var ticketId: Long,
    var profileId: Long,
    var ean: String,
    var priorityLevel: Int,
    var expertId: Long,
    var status: String,
    var creationDate: Date
)

fun Ticket.toDTO(): TicketDTO {
    return TicketDTO(ticketId, profileId, ean, priorityLevel, expertId, status, creationDate)
}