package it.polito.wa2.g13.server.ticketing.ticketsHistory

import org.springframework.stereotype.Service

@Service
class TicketHistoryServiceImpl(
    private val ticketHistoryRepository: TicketHistoryRepository
) : TicketHistoryService {

}