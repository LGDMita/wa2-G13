package it.polito.wa2.g13.server.ticketing.ticketsHistory

import it.polito.wa2.g13.server.ticketing.tickets.Ticket
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name= "tickets_history")
class TicketHistory(
    @Id
    var historyId: Long,
    @ManyToOne
    @JoinColumn(name = "ticketId")
    var ticket: Ticket,
    var oldStatus: String,
    var newStatus: String,
    var dateTime: Date
)

fun TicketHistory.toDTO(): TicketHistoryDTO {
    return TicketHistoryDTO(historyId, ticket, oldStatus, newStatus, dateTime)
}