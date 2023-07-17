package it.polito.wa2.g13.server.ticketing.messages

import it.polito.wa2.g13.server.ticketing.attachments.AttachmentService
import it.polito.wa2.g13.server.ticketing.tickets.TicketNotFoundException
import it.polito.wa2.g13.server.ticketing.tickets.TicketRepository
import it.polito.wa2.g13.server.ticketing.tickets.TicketService
import it.polito.wa2.g13.server.ticketing.tickets.toTicket
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class MessageServiceImpl(
    private val messageRepository: MessageRepository,
    private val attachmentService: AttachmentService,
    private val ticketRepository: TicketRepository
) : MessageService {

    override fun getMessages(ticketId: Long): List<MessageDTO> {
        val ticket= ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()
        return messageRepository.findByTicket(ticket).map { it.toDTO() }
    }
    @Transactional
    override fun sendMessage(fromUser: Boolean,text: String, atts: List<MultipartFile>,ticketId: Long): Long? {
        val ticket= ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()
        val attachments=atts.map{a : MultipartFile -> attachmentService.createAttachment(a)}.toMutableSet()
        val message=Message(fromUser= fromUser, text = text, datetime = Date(), attachments = attachments)
        ticket.addMessage(message)
        messageRepository.save(message)
        return message.getId()
    }
}