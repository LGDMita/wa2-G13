package it.polito.wa2.g13.server.ticketing.tickets

import it.polito.wa2.g13.server.ticketing.experts.ExpertNotFoundException
import it.polito.wa2.g13.server.ticketing.experts.ExpertRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.validation.BindingResult

@Service
class TicketServiceImpl(
    private val ticketRepository: TicketRepository,
    private val expertRepository: ExpertRepository
) : TicketService {

    override fun createTicket(ticketDTO: TicketDTO, br: BindingResult): TicketDTO? {
        return if(!ticketRepository.existsById(ticketDTO.toTicket().ticketId)){
            return ticketRepository.save(ticketDTO.toTicket()).toDTO()
        } else
            null
    }

    override fun changeStatus(ticketId: Long, status: String): Boolean {
        val ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()

        if(ticket.status == status)
            return true

        if(!stateChangeChecker(ticket.status, status))
            throw StateChangeNotAllowedException()

        ticketRepository.save(Ticket(
            ticketId = ticketId,
            profile = ticket.profile,
            ean = ticket.ean,
            priorityLevel = ticket.priorityLevel,
            expert = ticket.expert,
            status = status,
            creationDate = ticket.creationDate
        ))

        return true
    }

    override fun changePriority(ticketId: Long, priorityLevel: Int): Boolean {
        val ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()

        if(ticket.priorityLevel == priorityLevel)
            return true

        ticketRepository.save(Ticket(
            ticketId = ticketId,
            profile = ticket.profile,
            ean = ticket.ean,
            priorityLevel = priorityLevel,
            expert = ticket.expert,
            status = ticket.status,
            creationDate = ticket.creationDate
        ))

        return true
    }

    override fun changeExpert(ticketId: Long, expertId: Long): Boolean {
        val ticket = ticketRepository.findByIdOrNull(ticketId)
        val expert = expertRepository.findByIdOrNull((expertId))

        if(ticket == null)
            throw TicketNotFoundException()
        if(expert == null)
            throw ExpertNotFoundException()
        if(ticket.expert.expertId == expertId)
            return true

        ticketRepository.save(Ticket(
            ticketId = ticketId,
            profile = ticket.profile,
            ean = ticket.ean,
            priorityLevel = ticket.priorityLevel,
            expert = expert,
            status = ticket.status,
            creationDate = ticket.creationDate
        ))

        return true
    }

}