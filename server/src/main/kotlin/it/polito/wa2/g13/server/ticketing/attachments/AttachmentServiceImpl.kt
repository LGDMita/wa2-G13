package it.polito.wa2.g13.server.ticketing.attachments

import it.polito.wa2.g13.server.profiles.ProfileController
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class AttachmentServiceImpl(
    private val attachmentRepository: AttachmentRepository
) : AttachmentService {

    private val log = LoggerFactory.getLogger(ProfileController::class.java)

    override fun createAttachment(attachment: MultipartFile): Attachment {
        log.info("Creating attachment. Attachment data: {}", attachment)
        val attachmentName: String = if (attachment.originalFilename!=null) attachment.originalFilename!!
        else attachment.name
        return Attachment(
            size = attachment.size,
            type = attachment.contentType!!,
            attachmentName = attachmentName,
            dataBin = attachment.bytes,
            datetime = Date()
        )
    }
}