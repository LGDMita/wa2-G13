package it.polito.wa2.g13.server.ticketing.attachments

import java.util.*

data class AttachmentDTO(
    var attachmentId: Long,
    var type: String,
    var size: Long,
    var dataBin: Byte,
    var datetime: Date
)

fun Attachment.toDTO(): AttachmentDTO {
    return AttachmentDTO(attachmentId, type, size, dataBin, datetime)
}