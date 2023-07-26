package it.polito.wa2.g13.server.ticketing.attachments

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class AttachmentServiceImpl(
    private val attachmentRepository: AttachmentRepository
) : AttachmentService {

    override fun createAttachment(attachment: MultipartFile): Attachment {
        var attachmentName: String
        if (attachment.originalFilename!=null) attachmentName= attachment.originalFilename!!
        else attachmentName=attachment.name
        return Attachment(
            size = attachment.size,
            type = attachment.contentType!!,
            attachmentName = attachmentName,
            dataBin = attachment.bytes,
            datetime = Date()
        )
    }
}