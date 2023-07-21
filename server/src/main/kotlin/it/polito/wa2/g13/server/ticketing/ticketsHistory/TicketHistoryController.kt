package it.polito.wa2.g13.server.ticketing.ticketsHistory

import io.micrometer.observation.annotation.Observed
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@Observed
@Slf4j
class TicketHistoryController(
    private val ticketHistoryService: TicketHistoryService
) {

    private val log = LoggerFactory.getLogger(TicketHistoryController::class.java)

    @GetMapping("/API/tickets/{ticket}/history")
    fun getHistory(@PathVariable ticket: Long): List<TicketHistoryDTO> {
        log.info("Requiring history of ticket with ticketId: {}", ticket)
        return ticketHistoryService.getHistory(ticket)
    }
}
