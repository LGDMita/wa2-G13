package it.polito.wa2.g13.ticketing.messagesAttachments

data class MessageAttachmentDTO(
    var messageId: Long,
    var attachmentId: Long
)

fun MessageAttachment.toDTO(): MessageAttachmentDTO {
    return MessageAttachmentDTO(messageId, attachmentId)
}