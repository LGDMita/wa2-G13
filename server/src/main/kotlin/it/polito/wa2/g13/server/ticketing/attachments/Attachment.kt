package it.polito.wa2.g13.server.ticketing.attachments

import it.polito.wa2.g13.server.ticketing.messages.Message
import jakarta.persistence.*
import java.util.Date

@Entity
@Table(name= "attachments")
class Attachment(
    @Id
    var attachmentId: Long,
    @ManyToOne
    @JoinColumn(name = "messageId")
    var message: Message,
    var type: String,
    var size: Long,
    var dataBin: ByteArray,
    var datetime: Date)

fun AttachmentDTO.toAttachment(): Attachment {
    return Attachment(attachmentId, message, type, size, dataBin, datetime)
}