package it.polito.wa2.g13.server.ticketing.attachments

import it.polito.wa2.g13.server.ticketing.messages.Message
import java.util.*

data class AttachmentDTO(
    var attachmentId: Long,
    var message: Message,
    var type: String,
    var size: Long,
    var dataBin: ByteArray,
    var datetime: Date
)

fun Attachment.toDTO(): AttachmentDTO {
    return AttachmentDTO(attachmentId, message, type, size, dataBin, datetime)
}