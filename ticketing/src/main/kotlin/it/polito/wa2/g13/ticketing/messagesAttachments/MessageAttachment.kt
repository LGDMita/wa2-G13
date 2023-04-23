package it.polito.wa2.g13.ticketing.messagesAttachments

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name= "messagesAttachments")
class MessageAttachment(
    @Id
    var messageId: Long,
    var attachmentId: Long
) {

}

fun MessageAttachmentDTO.toMessageAttachment(): MessageAttachment {
    return MessageAttachment(messageId, attachmentId)
}
