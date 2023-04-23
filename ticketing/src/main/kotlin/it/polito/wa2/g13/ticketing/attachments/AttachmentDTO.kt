package it.polito.wa2.g13.ticketing.attachments

import jakarta.persistence.Id
import java.util.*

data class AttachmentDTO(
    var attachmentId: Long,
    var messageId: Long,
    var type: String,
    var size: Long,
    var data_bin: Byte,
    var datetime: Date
)

fun Attachment.toDTO(): AttachmentDTO {
    return AttachmentDTO(attachmentId, messageId, type, size, data_bin, datetime)
}