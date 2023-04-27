package it.polito.wa2.g13.server.ticketing.tickets

import org.springframework.stereotype.Service
import org.springframework.validation.BindingResult

@Service
interface TicketService {

    fun createTicket(ticketPostDTO: TicketPostDTO, br: BindingResult): TicketDTO?
    fun changeStatus(ticketId: Long, status: String): Boolean
    fun changePriority(ticketId: Long, priorityLevel: Int): Boolean
    fun changeExpert(ticketId: Long, expertId: Long): Boolean
}