package it.polito.wa2.g13.server.ticketing.tickets

import org.springframework.stereotype.Service

@Service
interface TicketService {
    fun ticketExist(id: Long) : Boolean
    fun getTicket(id: Long) : TicketDTO?
}