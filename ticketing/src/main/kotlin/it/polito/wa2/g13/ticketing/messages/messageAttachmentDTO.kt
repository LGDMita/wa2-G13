package it.polito.wa2.g13.ticketing.messages

import java.util.*

data class MessageDTO(
    var messageId: Long,
    var ticketId: Long,
    var fromUser: Boolean,
    var text: String,
    var datetime: Date
)

fun Message.toDTO(): MessageDTO {
    return MessageDTO(messageId, ticketId, fromUser, text, datetime)
}