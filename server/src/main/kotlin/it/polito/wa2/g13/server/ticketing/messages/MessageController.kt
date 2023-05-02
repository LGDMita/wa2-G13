package it.polito.wa2.g13.server.ticketing.messages

import it.polito.wa2.g13.server.ticketing.attachments.Attachment
import it.polito.wa2.g13.server.ticketing.tickets.Ticket
import org.apache.tomcat.util.http.parser.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Validated
@RestController
class MessageController(
    private val messageService: MessageService
) {


    //post: /API/tickets/:id/messages
    //body: {from_user: from_user, text: text, attachments: attachments}
    @PostMapping("/API/tickets/{ticket}/messages", consumes = [org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE])
    fun sendMessage(@RequestPart attachments: MutableList<MultipartFile>,
                    @RequestParam fromUser: Boolean,
                    @RequestParam text: String,
                    @PathVariable ticket: Long) : Long? {
        return messageService.sendMessage(fromUser,text,attachments,ticket)
    }
    //get: /API/tickets/:id/messages
    @GetMapping("/API/tickets/{ticket}/messages")
    fun getMessages(@PathVariable ticket: Long) : List<MessageDTO>{
        return messageService.getMessages(ticket)
    }
}
