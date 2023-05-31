package it.polito.wa2.g13.server.ticketing.messages

import io.micrometer.observation.annotation.Observed
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Validated
@RestController
@Observed
@Slf4j
class MessageController(
    private val messageService: MessageService
) {

    private val log = LoggerFactory.getLogger(MessageController::class.java)

    //post: /API/tickets/:id/messages
    //body: {from_user: from_user, text: text, attachments: attachments}
    @PostMapping("/API/tickets/{ticket}/messages", consumes = [org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE])
    fun sendMessage(@RequestPart attachments: MutableList<MultipartFile>,
                    @RequestParam fromUser: Boolean,
                    @RequestParam text: String,
                    @PathVariable ticket: Long) : Long? {
        val attachmentsNew = attachments.joinToString(",", "[", "]") { it.toString() }
        log.info("Sending messages attachments:{}, fromUser:{}, text:{}, ticketId:{}", attachmentsNew, fromUser.toString(), text.replace(" ", "_"), ticket.toString())
        return messageService.sendMessage(fromUser,text,attachments,ticket)
    }
    //get: /API/tickets/:id/messages
    @GetMapping("/API/tickets/{ticket}/messages")
    fun getMessages(@PathVariable ticket: Long) : List<MessageDTO>{
        log.info("Returning message with id:{}", ticket)
        return messageService.getMessages(ticket)
    }
}
