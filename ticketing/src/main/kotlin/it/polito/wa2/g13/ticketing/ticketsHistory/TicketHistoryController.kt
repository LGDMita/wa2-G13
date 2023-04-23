package it.polito.wa2.g13.ticketing.ticketsHistory

import org.springframework.web.bind.annotation.*

@RestController
class TicketHistoryController(
    private val ticketHistoryService: TicketHistoryService
) {

}
