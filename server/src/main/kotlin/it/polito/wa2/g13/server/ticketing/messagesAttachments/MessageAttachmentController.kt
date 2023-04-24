package it.polito.wa2.g13.ticketing.messagesAttachments

import org.springframework.web.bind.annotation.*

@RestController
class MessageAttachmentController(
    private val messageAttachmentService: MessageAttachmentService
) {

}
