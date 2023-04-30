package it.polito.wa2.g13.server.ticketing.ticketsHistory

import org.springframework.stereotype.Service

@Service
interface TicketHistoryService {
    fun getHistory(ticket: Long): List<TicketHistoryDTO>
}