package it.polito.wa2.g13.ticketing.messagesAttachments

import it.polito.wa2.g13.server.ticketing.attachments.Attachment
import it.polito.wa2.g13.server.ticketing.messages.Message

data class MessageAttachmentDTO(
    var messageAttachmentId: Long,
    var message: Message,
    var attachment: Attachment
)

fun MessageAttachment.toDTO(): MessageAttachmentDTO {
    return MessageAttachmentDTO(messageAttachmentId, message, attachment)
}