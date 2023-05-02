package it.polito.wa2.g13.server.ticketing.tickets

import it.polito.wa2.g13.server.profiles.Profile
import it.polito.wa2.g13.server.ticketing.experts.Expert
import org.springframework.stereotype.Service
import java.util.*
import org.springframework.validation.BindingResult

@Service
interface TicketService {
    fun getTickets(): List<Ticket>

    fun getTicket(ticketId: Long): TicketDTO?

    fun modifyTicket(ticketId: Long, ticket: TicketDTO): Boolean

    fun getFilteredTickets(
        ean: String?,
        profileId: String?,
        priorityLevel: Int?,
        expertId: Long?,
        status: String?,
        creationDateStart: Date?,
        creationDateStop: Date?
    ): List<TicketDTO>

    /**
     * Creates a new ticket associated to a product and a profile
     */
    fun createTicket(ticketPostDTO: TicketPostDTO, br: BindingResult): TicketDTO?

    /**
     * Changes the status of an existing ticket
     */
    fun changeStatus(ticketId: Long, newStatus: String): Boolean

    /**
     * Changes the priority level of an existing ticket
     */
    fun changePriority(ticketId: Long, priorityLevel: Int): Boolean

    /**
     * Changes the expert that is responsible for an existing ticket
     */
    fun changeExpert(ticketId: Long, expertId: Long): Boolean
}