package it.polito.wa2.g13.server.ticketing.ticketsHistory

import it.polito.wa2.g13.server.ticketing.tickets.Ticket
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name= "ticketsHistory")
class TicketHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tickets_history_generator")
    @SequenceGenerator(name = "tickets_history_generator", sequenceName = "tickets_history_seq", allocationSize = 1)
    var historyId: Long = 1,
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