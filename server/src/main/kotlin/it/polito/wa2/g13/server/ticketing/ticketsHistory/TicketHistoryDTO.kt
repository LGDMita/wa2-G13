package it.polito.wa2.g13.server.ticketing.ticketsHistory

import it.polito.wa2.g13.server.ticketing.tickets.Ticket
import java.util.Date

class TicketHistoryDTO(
    var historyId: Long,
    var ticket: Ticket,
    var oldStatus: String,
    var newStatus: String,
    var dateTime: Date
) {

}

fun TicketHistoryDTO.toTicket(): TicketHistory {
    return TicketHistory(historyId, ticket, oldStatus, newStatus, dateTime)
}
