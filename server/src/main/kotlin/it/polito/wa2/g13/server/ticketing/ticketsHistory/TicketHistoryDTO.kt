package it.polito.wa2.g13.server.ticketing.ticketsHistory

import it.polito.wa2.g13.server.ticketing.tickets.Ticket
import java.util.Date

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name= "ticketsHistory")
class TicketHistoryDTO(
    @Id
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
