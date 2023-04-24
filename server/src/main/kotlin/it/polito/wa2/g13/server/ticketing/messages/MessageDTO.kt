package it.polito.wa2.g13.server.ticketing.messages

import it.polito.wa2.g13.server.ticketing.tickets.Ticket
import java.util.*

data class MessageDTO(
    var messageId: Long,
    var ticket: Ticket,
    var fromUser: Boolean,
    var text: String,
    var datetime: Date
)

fun Message.toDTO(): MessageDTO {
    return MessageDTO(messageId, ticket, fromUser, text, datetime)
}