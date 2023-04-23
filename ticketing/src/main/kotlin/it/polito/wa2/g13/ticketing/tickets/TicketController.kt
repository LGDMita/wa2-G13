package it.polito.wa2.g13.ticketing.tickets

import org.springframework.web.bind.annotation.*

@RestController
class TicketController(
    private val ticketService: TicketService
) {

}
