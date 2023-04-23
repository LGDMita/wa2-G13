package it.polito.wa2.g13.ticketing.messages

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name= "messages")
class Message(
    @Id
    var messageId: Long,
    var ticketId: Long,
    var fromUser: Boolean,
    var text: String,
    var datetime: Date
) {}

fun MessageDTO.toMessage(): Message {
    return Message(messageId, ticketId, fromUser, text, datetime)
}
