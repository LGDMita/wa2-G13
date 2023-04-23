package it.polito.wa2.g13.ticketing.messages

import org.springframework.web.bind.annotation.*

@RestController
class MessageController(
    private val messageService: MessageService
) {

}
