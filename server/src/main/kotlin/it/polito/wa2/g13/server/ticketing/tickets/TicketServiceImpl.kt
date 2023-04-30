package it.polito.wa2.g13.server.ticketing.tickets

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TicketServiceImpl(
    private val ticketRepository: TicketRepository
) : TicketService {
    override fun ticketExist(id: Long) : Boolean {
        return ticketRepository.existsById(id)
    }

    override fun getTicket(id: Long) : TicketDTO? {
        return ticketRepository.findByIdOrNull(id)?.toDTO()
    }
}