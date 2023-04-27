package it.polito.wa2.g13.server.ticketing.tickets

import it.polito.wa2.g13.server.profiles.Profile
import it.polito.wa2.g13.server.ticketing.experts.Expert
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class TicketServiceImpl(
    private val ticketRepository: TicketRepository
) : TicketService {

    override fun getTickets(): List<Ticket> {
        return ticketRepository.findAll()
    }

    override fun getTicket(ticketId: Long): TicketDTO? {
        return ticketRepository.findByIdOrNull(ticketId.toString())?.toDTO()
    }

    override fun modifyTicket(ticketId: Long, ticket: TicketDTO): Boolean {
        return if (ticketRepository.existsById(ticketId.toString())) {
            ticketRepository.deleteById(ticketId.toString())
            ticketRepository.save(ticket.toTicket())
            true
        } else {
            false
        }
    }

    override fun getFilteredTickets(
        ean: String?,
        profileId: String?,
        priorityLevel: Int?,
        expertId: Long?,
        status: String?,
        creationDateStart: Date?,
        creationDateStop: Date?
    ): List<TicketDTO> {
        return ticketRepository.getFilteredTickets(ean, profileId, priorityLevel, expertId, status, creationDateStart, creationDateStop)
    }
}