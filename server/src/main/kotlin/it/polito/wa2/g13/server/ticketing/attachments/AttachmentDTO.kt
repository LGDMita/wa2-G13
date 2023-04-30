package it.polito.wa2.g13.server.ticketing.attachments

import it.polito.wa2.g13.server.ticketing.messages.Message
import java.util.*

data class AttachmentDTO(
    var attachmentId: Long?,
    var messageId: Long?,
    var type: String,
    var size: Long,
    var dataBin: ByteArray,
    var datetime: Date
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AttachmentDTO

        if (attachmentId != other.attachmentId) return false
        if (messageId != other.messageId) return false
        if (type != other.type) return false
        if (size != other.size) return false
        if (!dataBin.contentEquals(other.dataBin)) return false
        return datetime == other.datetime
    }

    override fun hashCode(): Int {
        var result = attachmentId?.hashCode() ?: 0
        result = 31 * result + (messageId?.hashCode() ?: 0)
        result = 31 * result + type.hashCode()
        result = 31 * result + size.hashCode()
        result = 31 * result + dataBin.contentHashCode()
        result = 31 * result + datetime.hashCode()
        return result
    }
}

fun Attachment.toDTO(): AttachmentDTO {
    return AttachmentDTO(getId(), message?.getId(), type, size, dataBin, datetime)
}