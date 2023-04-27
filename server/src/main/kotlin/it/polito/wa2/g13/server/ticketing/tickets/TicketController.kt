package it.polito.wa2.g13.server.ticketing.tickets

import jakarta.validation.Valid
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@RestController
class TicketController(
    private val ticketService: TicketService
) {
    @PostMapping("/API/tickets")
    fun createTicket(
        @RequestBody @Valid ticketPostDTO: TicketPostDTO,
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

        if(status !is String)
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

        if(priorityLevel !is Int)
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
