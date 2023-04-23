package it.polito.wa2.g13.ticketing.attachments

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name= "attachments")
class Attachment(
    @Id var attachmentId: Long,
    var messageId: Long,
    var type: String,
    var size: Long,
    var data_bin: Byte,
    var dateTime: Date) {

}

fun AttachmentDTO.toAttachment(): Attachment {
    return Attachment(attachmentId, messageId, type, size, data_bin, dateTime)
}