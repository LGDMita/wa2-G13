package it.polito.wa2.g13.server.ticketing.messages

import it.polito.wa2.g13.server.ticketing.tickets.Ticket
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name= "messages")
class Message(
    @Id
    var messageId: Long,
    @ManyToOne
    @JoinColumn(name = "ticketId")
    var ticket: Ticket,
    var fromUser: Boolean,
    var text: String,
    var datetime: Date
) {}

fun MessageDTO.toMessage(): Message {
    return Message(messageId, ticket, fromUser, text, datetime)
}
