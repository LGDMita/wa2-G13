package it.polito.wa2.g13.server.ticketing.tickets

import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.util.Date

@RestController
class TicketController(
    private val ticketService: TicketService
) {
    @GetMapping("/API/tickets")
    fun getTickets(): List<Ticket>? {
        return ticketService.getTickets()
    }

    @GetMapping("/API/tickets/{ticketId}")
    fun getTicket(@PathVariable("ticketId") ticketId: Long): TicketDTO? {
        return ticketService.getTicket(ticketId) ?: throw TicketNotFoundException()
    }

    @GetMapping("/API/tickets/")
    fun getFilteredTickets(
        @RequestParam("ean") ean: String,
        @RequestParam("profileId") profileId: String,
        @RequestParam("priorityLevel") priorityLevel: Int,
        @RequestParam("expertId") expertId: Long,
        @RequestParam("status") status: String,
        @RequestParam("creationDateStart") @DateTimeFormat(pattern = "yyyy-mm-dd")
        creationDateStart: Date,
        @RequestParam("creationDateStop") @DateTimeFormat(pattern = "yyyy-mm-dd")
        creationDateStop: Date,
    ): List<TicketDTO> {
        return ticketService.getFilteredTickets(ean, profileId, priorityLevel, expertId, status, creationDateStart, creationDateStop)
    }

/*    @GetMapping("/API/tickets/?ean={ean}&profileId={profileId}&priorityLevel={priorityLevel}&" +
            "expertId={expertId}&status={status}&creationDateStart={creationDateStart}&creationDateStop={creationDateStop}")
    fun getFilteredTickets(
        @PathVariable("ean") ean: String,
        @PathVariable("profileId") profileId: String,
        @PathVariable("priorityLevel") priorityLevel: Int,
        @PathVariable("expertId") expertId: Long,
        @PathVariable("status") status: String,
        @PathVariable("creationDateStart") creationDateStart: Date,
        @PathVariable("creationDateStop") creationDateStop: Date,
    ): List<TicketDTO> {
        return ticketService.getFilteredTickets(ean, profileId, priorityLevel, expertId, status, creationDateStart, creationDateStop)
    }*/

    @PutMapping("/API/ticket/")
    fun modifyTicket(
        @RequestParam("ticketId") ticketId: Long,
        @RequestBody ticketDTO: TicketDTO
    ): Boolean {
        return ticketService.modifyTicket(ticketId, ticketDTO)
    }
}
