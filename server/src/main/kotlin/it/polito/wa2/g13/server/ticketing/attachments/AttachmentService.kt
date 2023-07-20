package it.polito.wa2.g13.server.ticketing.attachments

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
interface AttachmentService {
    fun createAttachment(attachment: MultipartFile): Attachment
}