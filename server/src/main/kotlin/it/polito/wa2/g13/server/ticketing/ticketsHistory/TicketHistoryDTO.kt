package it.polito.wa2.g13.server.ticketing.ticketsHistory

import java.util.Date

data class TicketHistoryDTO(
    var historyId: Long?,
    var ticket: Long?,
    var oldStatus: String,
    var newStatus: String,
    var dateTime: Date
)

fun TicketHistory.toDTO(): TicketHistoryDTO {
    return TicketHistoryDTO(getId(), ticket.getId(), oldStatus, newStatus, dateTime)
}