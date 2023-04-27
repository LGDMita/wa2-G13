package it.polito.wa2.g13.server.ticketing.messages

import org.springframework.stereotype.Service

@Service
class MessageServiceImpl(
    private val messageRepository: MessageRepository
) : MessageService