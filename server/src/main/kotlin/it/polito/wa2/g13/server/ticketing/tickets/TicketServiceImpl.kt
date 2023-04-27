package it.polito.wa2.g13.server.ticketing.tickets

import it.polito.wa2.g13.server.products.ProductNotFoundException
import it.polito.wa2.g13.server.products.ProductRepository
import it.polito.wa2.g13.server.profiles.ProfileNotFoundException
import it.polito.wa2.g13.server.profiles.ProfileRepository
import it.polito.wa2.g13.server.ticketing.experts.ExpertNotFoundException
import it.polito.wa2.g13.server.ticketing.experts.ExpertRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.validation.BindingResult
import java.util.*

@Service
class TicketServiceImpl(
    private val ticketRepository: TicketRepository,
    private val expertRepository: ExpertRepository,
    private val profileRepository: ProfileRepository,
    private val productRepository: ProductRepository
) : TicketService {

    override fun createTicket(ticketPostDTO: TicketPostDTO, br: BindingResult): TicketDTO? {
        if(ticketRepository.existsById(ticketPostDTO.ticketId))
            throw DuplicateTicketException()

        val profile = profileRepository.findByIdOrNull(ticketPostDTO.profileId) ?: throw ProfileNotFoundException()
        val product = productRepository.findByIdOrNull(ticketPostDTO.ean) ?: throw ProductNotFoundException()


        return ticketRepository.save(Ticket(
            ticketId = ticketPostDTO.ticketId,
            profile = profile,
            product = product,
            priorityLevel = null,
            expert = null,
            status = null,
            creationDate = Date()
        )).toDTO()

    }

    override fun changeStatus(ticketId: Long, status: String): Boolean {
        val ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()
        val oldStatus = ticket.status

        if(oldStatus == status)
            return true

        if(oldStatus != null && !stateChangeChecker(oldStatus, status))
                throw StateChangeNotAllowedException()

        ticketRepository.save(Ticket(
            ticketId = ticketId,
            profile = ticket.profile,
            product = ticket.product,
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
            product = ticket.product,
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
        if(ticket.expert?.expertId == expertId)
            return true

        ticketRepository.save(Ticket(
            ticketId = ticketId,
            profile = ticket.profile,
            product = ticket.product,
            priorityLevel = ticket.priorityLevel,
            expert = expert,
            status = ticket.status,
            creationDate = ticket.creationDate
        ))

        return true
    }

}