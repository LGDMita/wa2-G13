package it.polito.wa2.g13.server.ticketing.tickets

import jakarta.validation.Valid
import jakarta.validation.constraints.*
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
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
        @RequestBody @Valid ticketDTO: TicketDTO,
        br: BindingResult
    ): Boolean {
        if (!br.hasErrors()) {
            if (ticketService.getTicket(ticketDTO.ticketId) != null) {
                return ticketService.modifyTicket(ticketDTO)
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
        @Pattern(regexp = "(open|closed|resolved|in_progress|reopened)")
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
    @PostMapping("/API/tickets")
    @ResponseStatus(HttpStatus.CREATED)
    fun createTicket(
        @Valid @RequestBody(required = true) ticketPostDTO: TicketPostDTO,
        br: BindingResult
    ): TicketDTO? {
        if (!br.hasErrors()) {
            return ticketService.createTicket(ticketPostDTO, br)
        }
        else
            throw InvalidTicketArgumentsException()
    }

    @PutMapping("/API/tickets/{ticketId}/changeStatus")
    fun changeStatus(
        @PathVariable ticketId: Int,
        @RequestBody req: Map<String, Any>
    ): Boolean {
        val status = req["status"] ?: throw InvalidTicketArgumentsException()

        if(status !is String || !listOf("open", "in_progress", "reopened", "resolved", "closed").contains(status))
            throw InvalidTicketArgumentsException()

        println("Changing status of ticket $ticketId to $status")

        return ticketService.changeStatus(ticketId.toLong(), status)
    }

    @PutMapping("/API/tickets/{ticketId}/changePriority")
    fun changePriority(
        @PathVariable ticketId: Int,
        @RequestBody req: Map<String, Any>
    ): Boolean {
        val priorityLevel = req["priorityLevel"] ?: throw InvalidTicketArgumentsException()

        if(priorityLevel !is Int || priorityLevel > 4 || priorityLevel < 0)
            throw InvalidTicketArgumentsException()

        println("Changing priority level of ticket $ticketId to $priorityLevel")

        return ticketService.changePriority(ticketId.toLong(), priorityLevel)
    }

    @PutMapping("/API/tickets/{ticketId}/changeExpert")
    fun changeExpert(
        @PathVariable ticketId: Int,
        @RequestBody req: Map<String, Any>
    ): Boolean {
        val expertId = req["expertId"] ?: throw InvalidTicketArgumentsException()

        if(expertId !is Int)
            throw InvalidTicketArgumentsException()

        println("Changing expert assigned to ticket $ticketId to $expertId")

        return ticketService.changeExpert(ticketId.toLong(), expertId.toLong())
    }
}
