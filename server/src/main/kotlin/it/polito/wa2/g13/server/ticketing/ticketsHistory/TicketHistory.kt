package it.polito.wa2.g13.server.ticketing.ticketsHistory

import it.polito.wa2.g13.server.EntityBase
import it.polito.wa2.g13.server.ticketing.tickets.Ticket
import it.polito.wa2.g13.server.ticketing.tickets.toDTO
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name= "ticketsHistory")
class TicketHistory(
    @ManyToOne
    var ticket: Ticket?=null,
    var oldStatus: String,
    var newStatus: String,
    var dateTime: Date,
    setId: Long?=null
) : EntityBase<Long>(setId)

fun TicketHistory.toDTO(): TicketHistoryDTO {
    return TicketHistoryDTO(getId(), ticket?.ticketId, oldStatus, newStatus, dateTime)
}