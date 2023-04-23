package it.polito.wa2.g13.ticketing.ticketsHistory

import java.util.*

data class TicketHistory(
    var historyId: Long,
    var ticketId: Long,
    var oldStatus: String,
    var newStatus: String,
    var dateTime: Date
)

fun TicketHistory.toDTO(): TicketHistoryDTO {
    return TicketHistoryDTO(historyId, ticketId, oldStatus, newStatus, dateTime)
}