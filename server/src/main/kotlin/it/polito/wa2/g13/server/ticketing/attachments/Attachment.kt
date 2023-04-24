package it.polito.wa2.g13.server.ticketing.attachments

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name= "attachments")
class Attachment(
    @Id var attachmentId: Long,
    var type: String,
    var size: Long,
    var dataBin: Byte,
    var datetime: Date) {

}

fun AttachmentDTO.toAttachment(): Attachment {
    return Attachment(attachmentId, type, size, dataBin, datetime)
}