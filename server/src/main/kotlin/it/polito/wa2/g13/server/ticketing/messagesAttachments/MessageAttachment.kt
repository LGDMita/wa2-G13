package it.polito.wa2.g13.ticketing.messagesAttachments

import it.polito.wa2.g13.server.ticketing.attachments.Attachment
import it.polito.wa2.g13.server.ticketing.messages.Message
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name= "messagesAttachments")
class MessageAttachment(
    @Id
    var messageAttachmentId: Long,
    @ManyToOne
    var message: Message,
    @ManyToOne
    var attachment: Attachment
) {

}

fun MessageAttachmentDTO.toMessageAttachment(): MessageAttachment {
    return MessageAttachment(messageAttachmentId, message, attachment)
}
