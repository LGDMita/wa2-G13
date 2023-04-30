package it.polito.wa2.g13.server.ticketing.ticketsHistory

import it.polito.wa2.g13.server.ticketing.tickets.TicketNotFoundException
import it.polito.wa2.g13.server.ticketing.tickets.TicketService
import it.polito.wa2.g13.server.ticketing.tickets.toTicket
import org.springframework.stereotype.Service

@Service
class TicketHistoryServiceImpl(
    private val ticketHistoryRepository: TicketHistoryRepository,
    private val ticketService: TicketService
) : TicketHistoryService {


    override fun getHistory(ticketId: Long): List<TicketHistoryDTO>{
        val ticket= ticketService.getTicket(ticketId)?:throw TicketNotFoundException()
        return ticketHistoryRepository.findByTicketOrderByDateTime(ticket.toTicket()).map{it.toDTO()}
    }
}