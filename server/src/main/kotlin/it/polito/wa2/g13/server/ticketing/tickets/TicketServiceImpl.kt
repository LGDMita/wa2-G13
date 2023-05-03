package it.polito.wa2.g13.server.ticketing.tickets

import it.polito.wa2.g13.server.products.ProductNotFoundException
import it.polito.wa2.g13.server.products.ProductRepository
import it.polito.wa2.g13.server.profiles.ProfileNotFoundException
import it.polito.wa2.g13.server.profiles.ProfileRepository
import it.polito.wa2.g13.server.ticketing.experts.ExpertNotFoundException
import it.polito.wa2.g13.server.ticketing.experts.ExpertRepository
import it.polito.wa2.g13.server.ticketing.ticketsHistory.TicketHistory
import it.polito.wa2.g13.server.ticketing.ticketsHistory.TicketHistoryRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.validation.BindingResult
import java.util.*

@Service
class TicketServiceImpl(
    private val ticketRepository: TicketRepository,
    private val expertRepository: ExpertRepository,
    private val profileRepository: ProfileRepository,
    private val productRepository: ProductRepository,
    private val ticketHistoryRepository: TicketHistoryRepository
) : TicketService {
    override fun ticketExist(id: Long) : Boolean {
        return ticketRepository.existsById(id)
    }

    override fun getTickets(): List<Ticket> {
        return ticketRepository.findAll()
    }

    override fun getTicket(ticketId: Long): TicketDTO? {
        return ticketRepository.findByIdOrNull(ticketId)?.toDTO()
    }

    override fun modifyTicket(ticketId: Long, ticket: TicketDTO): Boolean {
        return if (ticketRepository.existsById(ticketId)) {
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
        return ticketRepository.getFilteredTickets(ean, profileId, priorityLevel, expertId, status, creationDateStart, creationDateStop).map { t -> t.toDTO() }
    }
    override fun createTicket(ticketPostDTO: TicketPostDTO, br: BindingResult): TicketDTO? {

        // If the profileId contained in the request doesn't exist throws exception
        val profile = profileRepository.findByIdOrNull(ticketPostDTO.profileId) ?: throw ProfileNotFoundException()

        // If the productId contained in the request doesn't exist throws exception
        val product = productRepository.findByIdOrNull(ticketPostDTO.ean) ?: throw ProductNotFoundException()

        // Contains the current date and time, that will be associated to the ticket
        val date = Date()

        // Saves the ticket in the repository
        val ticket = ticketRepository.save(Ticket(
            profile = profile,
            product = product,
            priorityLevel = null,
            expert = null,
            status = "open",
            creationDate = date,
            messages = mutableSetOf(),
        ))

        // Saves the ticket state change in the repository. This is not a real state change, both old and new status are "open", but it's useful to have it for debugging reasons
        ticketHistoryRepository.save(TicketHistory(
            ticket = ticket,
            oldStatus = ticket.status,
            newStatus = "open",
            dateTime = date
        ))

        return ticket.toDTO()

    }

    override fun changeStatus(ticketId: Long, newStatus: String): Boolean {

        // If the ticketId contained in the URI doesn't exist throws an exception
        val ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()

        // Takes the old status from the existing ticket
        val oldStatus = ticket.status

        // If the ticket is already in the state contained in the request, just return true
        if(oldStatus == newStatus)
            return true

        // If the state change is not allowed throws an exception
        if(!stateChangeChecker(oldStatus, newStatus))
                throw StateChangeNotAllowedException()

        // Updates the ticket with the new state, all th other values remain the same
        val updatedTicket = ticketRepository.save(Ticket(
            ticketId = ticketId,
            profile = ticket.profile,
            product = ticket.product,
            priorityLevel = ticket.priorityLevel,
            expert = ticket.expert,
            status = newStatus,
            creationDate = ticket.creationDate,
            messages = ticket.messages
        ))

        // Saves the state change in the repository
        ticketHistoryRepository.save(TicketHistory(
            ticket = updatedTicket,
            oldStatus = oldStatus,
            newStatus = newStatus,
            dateTime = Date()
        ))

        return true
    }

    override fun changePriority(ticketId: Long, priorityLevel: Int): Boolean {

        // If the ticketId contained in the URI doesn't exist throws an exception
        val ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()

        // If the ticket has already the priority level contained in the request, just return true
        if(ticket.priorityLevel == priorityLevel)
            return true

        // Updates the ticket with the new priority level, all the other values remain the same
        ticketRepository.save(Ticket(
            ticketId = ticketId,
            profile = ticket.profile,
            product = ticket.product,
            priorityLevel = priorityLevel,
            expert = ticket.expert,
            status = ticket.status,
            creationDate = ticket.creationDate,
            messages = ticket.messages
        ))

        return true
    }

    override fun changeExpert(ticketId: Long, expertId: Long): Boolean {

        // If the ticketId contained in the URI doesn't exist throws an exception
        val ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()

        // If the expertId contained in the request doesn't exist throws an exception
        val expert = expertRepository.findByIdOrNull((expertId)) ?: throw ExpertNotFoundException()

        // If the ticket is already managed by the expert whose id is contained in the request, just return true
        if(ticket.expert?.getId() == expertId)
            return true

        // Updates the ticket with the new priority level, all the other values remain the same
        ticketRepository.save(Ticket(
            ticketId = ticketId,
            profile = ticket.profile,
            product = ticket.product,
            priorityLevel = ticket.priorityLevel,
            expert = expert,
            status = ticket.status,
            creationDate = ticket.creationDate,
            messages = ticket.messages
        ))

        return true
    }

}