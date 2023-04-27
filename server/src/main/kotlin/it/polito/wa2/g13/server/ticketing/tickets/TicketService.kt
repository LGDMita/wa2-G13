package it.polito.wa2.g13.server.ticketing.tickets

import it.polito.wa2.g13.server.profiles.Profile
import it.polito.wa2.g13.server.ticketing.experts.Expert
import org.springframework.stereotype.Service
import java.util.*

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
}