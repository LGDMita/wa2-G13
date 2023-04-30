package it.polito.wa2.g13.server.ticketing.ticketsHistory

import it.polito.wa2.g13.server.ticketing.tickets.Ticket
import it.polito.wa2.g13.server.ticketing.tickets.TicketDTO
import it.polito.wa2.g13.server.ticketing.tickets.toTicket
import java.util.Date
data class TicketHistoryDTO(
    var historyId: Long?,
    var ticket: Long?,
    var oldStatus: String,
    var newStatus: String,
    var dateTime: Date
)

fun TicketHistoryDTO.toTicket(ticket: TicketDTO?): TicketHistory {
    return TicketHistory(ticket?.toTicket(), oldStatus, newStatus, dateTime,historyId)
}
