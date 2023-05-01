package it.polito.wa2.g13.server.ticketing.tickets

import jakarta.validation.Valid
import jakarta.validation.constraints.*
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
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

    @PutMapping("/API/ticket/")
    fun modifyTicket(
        @RequestParam("ticketId") ticketId: Long,
        @RequestBody @Valid ticketDTO: TicketDTO,
        br: BindingResult
    ): Boolean {
        if (!br.hasErrors()) {
            if (ticketService.getTicket(ticketId) != null) {
                return ticketService.modifyTicket(ticketId, ticketDTO)
            }
            else {
                throw TicketNotFoundException()
            }
        }
        else {
            throw InvalidTicketException()
        }
    }
}

@RestController
@Validated
class TicketControllerValidated(
    private val ticketService: TicketService
) {
    @GetMapping("/API/tickets/")
    fun getFilteredTickets(
        @RequestParam("ean")
        @Size(min=1, max=15, message = "Ean MUST be a NON empty string of max 15 chars")
        ean: String?,
        @RequestParam("profileId")
        @Email
        profileId: String?,
        @RequestParam("priorityLevel")
        @Min(value = 0, message = "Minimum value for priorityLevel is 0")
        @Max(value = 4, message = "Minimum value for priorityLevel is 4")
        priorityLevel: Int?,
        @RequestParam("expertId")
        expertId: Long?,
        @RequestParam("status")
        @Size(min=1, max=15, message = "Status MUST be a NON empty string of max 15 chars")
        status: String?,
        @RequestParam("creationDateStart")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        creationDateStart: Date?,
        @RequestParam("creationDateStop")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        creationDateStop: Date?,
    ): List<TicketDTO> {
        return ticketService.getFilteredTickets(
            ean,
            profileId,
            priorityLevel,
            expertId,
            status,
            creationDateStart,
            creationDateStop
        )
    }
}
