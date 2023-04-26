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

    fun filterTicket(
        ean: String,
        profile: Profile,
        priorityLevel: Int,
        expert: Expert,
        status: String,
        creationDate: Date
    ): List<Ticket>
}