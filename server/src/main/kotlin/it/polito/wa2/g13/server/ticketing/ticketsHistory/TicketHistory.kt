package it.polito.wa2.g13.server.ticketing.ticketsHistory

import it.polito.wa2.g13.server.EntityBase
import it.polito.wa2.g13.server.ticketing.tickets.Ticket
import it.polito.wa2.g13.server.ticketing.tickets.TicketDTO
import it.polito.wa2.g13.server.ticketing.tickets.toTicket
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "tickets_history")
class TicketHistory(
    @ManyToOne
    @JoinColumn(name = "ticketId")
    var ticket: Ticket,
    var oldStatus: String,
    var newStatus: String,
    var dateTime: Date,
    setId: Long? = null
) : EntityBase<Long>(setId) {
}

fun TicketHistoryDTO.toTicket(ticket: TicketDTO): TicketHistory {
    return TicketHistory(ticket.toTicket(), oldStatus, newStatus, dateTime, historyId)
}