package it.polito.wa2.g13.ticketing.messagesAttachments

import org.springframework.stereotype.Service

@Service
class MessageAttachmentServiceImpl(
    private val messageAttachmentRepository: MessageAttachmentRepository
) : MessageAttachmentService {

}