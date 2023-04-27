package it.polito.wa2.g13.server.ticketing.attachments

import org.springframework.web.bind.annotation.*

@RestController
class AttachmentController(
    private val attachmentService: AttachmentService
)
