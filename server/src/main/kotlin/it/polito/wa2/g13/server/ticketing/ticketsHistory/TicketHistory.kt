package it.polito.wa2.g13.server.ticketing.ticketsHistory

import it.polito.wa2.g13.server.ticketing.tickets.Ticket
import jakarta.persistence.ManyToOne
import java.util.*

data class TicketHistory(
    var historyId: Long,
    @ManyToOne
    var ticket: Ticket,
    var oldStatus: String,
    var newStatus: String,
    var dateTime: Date
)

fun TicketHistory.toDTO(): TicketHistoryDTO {
    return TicketHistoryDTO(historyId, ticket, oldStatus, newStatus, dateTime)
}