package it.polito.wa2.g13.server.ticketing.tickets

import org.springframework.web.bind.annotation.RestController

@RestController
class TicketController(
    private val ticketService: TicketService
) {

}
