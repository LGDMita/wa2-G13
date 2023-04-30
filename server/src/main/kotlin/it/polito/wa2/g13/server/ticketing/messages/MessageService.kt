package it.polito.wa2.g13.server.ticketing.messages

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
interface MessageService {

    fun getMessages(ticketId: Long): List<MessageDTO>
    fun sendMessage(fromUser: Boolean, text: String, atts: List<MultipartFile>, ticketId: Long): Long?
}