package it.polito.wa2.g13.ticketing.ticketsHistory

import java.util.Date

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name= "ticketsHistory")
class TicketHistoryDTO(
    @Id
    var historyId: Long,
    var ticketId: Long,
    var oldStatus: String,
    var newStatus: String,
    var dateTime: Date
) {

}

fun TicketHistoryDTO.toTicket(): TicketHistory {
    return TicketHistory(historyId, ticketId, oldStatus, newStatus, dateTime)
}
