package it.polito.wa2.g13.server.ticketing.ticketsHistory

import it.polito.wa2.g13.server.ticketing.tickets.TicketNotFoundException
import it.polito.wa2.g13.server.ticketing.tickets.TicketRepository
import it.polito.wa2.g13.server.ticketing.tickets.TicketService
import it.polito.wa2.g13.server.ticketing.tickets.toTicket
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TicketHistoryServiceImpl(
    private val ticketHistoryRepository: TicketHistoryRepository,
    private val ticketRepository: TicketRepository
) : TicketHistoryService {


    override fun getHistory(ticketId: Long): List<TicketHistoryDTO> {
        val ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()
        return ticketHistoryRepository.findByTicketOrderByDateTime(ticket).map { it.toDTO() }
    }
}