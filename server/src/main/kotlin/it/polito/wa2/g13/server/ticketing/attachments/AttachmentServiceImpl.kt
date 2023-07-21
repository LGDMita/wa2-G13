package it.polito.wa2.g13.server.ticketing.attachments

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class AttachmentServiceImpl(
    private val attachmentRepository: AttachmentRepository
) : AttachmentService {

    override fun createAttachment(attachment: MultipartFile): Attachment {
        return Attachment(
            size = attachment.size,
            type = attachment.contentType!!,
            dataBin = attachment.bytes,
            datetime = Date()
        )
    }
}