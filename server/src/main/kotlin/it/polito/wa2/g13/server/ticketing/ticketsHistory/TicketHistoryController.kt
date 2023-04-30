package it.polito.wa2.g13.server.ticketing.ticketsHistory

import org.springframework.web.bind.annotation.*

@RestController
class TicketHistoryController(
    private val ticketHistoryService: TicketHistoryService
) {

    @GetMapping("/API/tickets/{ticket}/history")
    fun getHistory(@PathVariable ticket: Long): List<TicketHistoryDTO>{
        return ticketHistoryService.getHistory(ticket)
    }
}
