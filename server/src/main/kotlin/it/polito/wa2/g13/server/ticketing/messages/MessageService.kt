package it.polito.wa2.g13.server.ticketing.messages

import org.springframework.stereotype.Service

@Service
interface MessageService {

    fun getMessages(ticketId: Long): List<MessageDTO>
    fun sendMessage(fromUser: Boolean, text: String, atts: List<MultipartFile>, ticketId: Long): Long?
}