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
) {

    override fun toString(): String {
        val attachmentsNew = attachments.joinToString(",", "[", "]") { it.toString() }
        val stringBuilder = StringBuilder()
        stringBuilder.append("messageId=").append(messageId).append("&")
        stringBuilder.append("ticketId=").append(ticketId).append("&")
        stringBuilder.append("fromUser=").append(fromUser).append("&")
        stringBuilder.append("text=").append(text.replace(" ", "_")).append("&")
        stringBuilder.append("datetime=").append(datetime).append("&")
        stringBuilder.append("attachments=").append(attachmentsNew)
        return stringBuilder.toString()
    }
}

fun Message.toDTO(): MessageDTO{
    return MessageDTO(getId(),ticket?.getId(),fromUser,text,datetime, attachments.map{it.toDTO()}.toMutableSet())
}