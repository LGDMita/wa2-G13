package it.polito.wa2.g13.server.ticketing.messages

import it.polito.wa2.g13.server.ticketing.attachments.AttachmentDTO
import it.polito.wa2.g13.server.ticketing.attachments.toDTO
import java.util.*

data class MessageDTO(
    val messageId: Long?,
    val ticketId: Long?,
    val fromUser: Boolean,
    val text: String,
    val datetime: Date,
    val attachments: MutableSet<AttachmentDTO>
)

fun Message.toDTO(): MessageDTO{
    return MessageDTO(getId(),ticket?.getId(),fromUser,text,datetime, attachments.map{it.toDTO()}.toMutableSet())
}