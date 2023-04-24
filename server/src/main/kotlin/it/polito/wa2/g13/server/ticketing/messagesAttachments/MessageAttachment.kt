package it.polito.wa2.g13.ticketing.messagesAttachments

import it.polito.wa2.g13.server.ticketing.attachments.Attachment
import it.polito.wa2.g13.server.ticketing.messages.Message
import jakarta.persistence.*

@Entity
@Table(name= "messagesAttachments")
class MessageAttachment(
    @Id
    var messageAttachmentId: Long,
    @ManyToOne
    @JoinColumn(name = "messageId")
    var message: Message,
    @ManyToOne
    @JoinColumn(name = "attachmentId")
    var attachment: Attachment
) {

}

fun MessageAttachmentDTO.toMessageAttachment(): MessageAttachment {
    return MessageAttachment(messageAttachmentId, message, attachment)
}
