package it.polito.wa2.g13.server.ticketing.messages

import it.polito.wa2.g13.server.ticketing.attachments.AttachmentService
import it.polito.wa2.g13.server.ticketing.attachments.toAttachment
import it.polito.wa2.g13.server.ticketing.tickets.TicketNotFoundException
import it.polito.wa2.g13.server.ticketing.tickets.TicketService
import it.polito.wa2.g13.server.ticketing.tickets.toTicket
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class MessageServiceImpl(
    private val messageRepository: MessageRepository,
    private val attachmentsService: AttachmentService,
    private val ticketService: TicketService
) : MessageService {

    override fun getMessages(ticketId: Long): List<MessageDTO> {
        val ticket= ticketService.getTicket(ticketId) ?: throw TicketNotFoundException()
        return messageRepository.findByTicket(ticket.toTicket()).map { it.toDTO() }
    }
    @Transactional
    override fun sendMessage(fromUser: Boolean,text: String, atts: List<MultipartFile>,ticketId: Long): Long? {
        val ticket= ticketService.getTicket(ticketId) ?: throw TicketNotFoundException()
        val attachments=atts.map{a : MultipartFile -> attachmentsService.createAttachment(a)}.toMutableSet()
        val message=Message(fromUser= fromUser, text = text, datetime = Date(), attachments = attachments)
        ticket.toTicket().addMessage(message)
        messageRepository.save(message)
        return message.getId()
    }
}