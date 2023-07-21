package it.polito.wa2.g13.server.ticketing.tickets

import it.polito.wa2.g13.server.products.ProductNotFoundException
import it.polito.wa2.g13.server.products.ProductRepository
import it.polito.wa2.g13.server.profiles.ProfileNotFoundException
import it.polito.wa2.g13.server.profiles.ProfileRepository
import it.polito.wa2.g13.server.ticketing.experts.ExpertNotFoundException
import it.polito.wa2.g13.server.ticketing.experts.ExpertRepository
import it.polito.wa2.g13.server.ticketing.ticketsHistory.TicketHistory
import it.polito.wa2.g13.server.ticketing.ticketsHistory.TicketHistoryRepository
import it.polito.wa2.g13.server.warranty.WarrantyNotBoughtException
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
    override fun ticketExist(id: Long): Boolean {
        return ticketRepository.existsById(id)
    }

    override fun getTickets(): List<Ticket> {
        return ticketRepository.findAll()
    }

    override fun getTicket(ticketId: Long): TicketDTO? {
        return ticketRepository.findByIdOrNull(ticketId)?.toDTO()
    }

    override fun modifyTicket(ticket: TicketDTO): Boolean {
        return if (ticketRepository.existsById(ticket.ticketId!!)) {
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
        expertId: String?,
        status: String?,
        creationDateStart: Date?,
        creationDateStop: Date?
    ): List<TicketDTO> {
        return ticketRepository.getFilteredTickets(
            ean,
            profileId,
            priorityLevel,
            expertId,
            status,
            creationDateStart,
            creationDateStop
        ).map { t -> t.toDTO() }
    }

    override fun createTicket(ticketPostDTO: TicketPostDTO, username: String, br: BindingResult): TicketDTO? {

        // If the email contained in the request doesn't exist throws exception
        val profile = profileRepository.findProfileByUsername(username) ?: throw ProfileNotFoundException()

        // If the productId contained in the request doesn't exist throws exception
        val product = productRepository.findByIdOrNull(ticketPostDTO.ean) ?: throw ProductNotFoundException()

        // If warranty not present can't open ticket
        if (!product.purchases.filter { it.profile == profile && it.warranty != null }
                .any { it.warranty?.datetimeExpire?.after(Date()) == true }) throw WarrantyNotBoughtException()

        // Contains the current date and time, that will be associated to the ticket
        val date = Date()

        // Saves the ticket in the repository
        val ticket = ticketRepository.save(
            Ticket(
                profile = profile,
                product = product,
                priorityLevel = null,
                expert = null,
                status = "open",
                creationDate = date,
                messages = mutableSetOf(),
            )
        )

        // Saves the ticket state change in the repository. This is not a real state change, both old and new status are "open", but it's useful to have it for debugging reasons
        ticketHistoryRepository.save(
            TicketHistory(
                ticket = ticket,
                oldStatus = ticket.status,
                newStatus = "open",
                dateTime = date
            )
        )

        return ticket.toDTO()

    }

    override fun changeStatus(ticketId: Long, newStatus: String, role: String?): Boolean {

        // If the ticketId contained in the URI doesn't exist throws an exception
        val ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()

        // Takes the old status from the existing ticket
        val oldStatus = ticket.status

        // If the ticket is already in the state contained in the request, just return true
        if (oldStatus == newStatus)
            return true

        // If the state change is not allowed throws an exception
        if (!stateChangeChecker(oldStatus, newStatus, role.toString()))
            throw StateChangeNotAllowedException()

        // Updates the ticket with the new state and saves it
        ticket.status = newStatus
        val updatedTicket = ticketRepository.save(ticket)

        // Saves the state change in the repository
        ticketHistoryRepository.save(
            TicketHistory(
                ticket = updatedTicket,
                oldStatus = oldStatus,
                newStatus = newStatus,
                dateTime = Date()
            )
        )

        return true
    }

    override fun changePriority(ticketId: Long, priorityLevel: Int): Boolean {

        // If the ticketId contained in the URI doesn't exist throws an exception
        val ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()

        // If the ticket has already the priority level contained in the request, just return true
        if (ticket.priorityLevel == priorityLevel)
            return true

        // Updates the ticket with the new priority level and saves it
        ticket.priorityLevel = priorityLevel
        ticketRepository.save(ticket)

        return true
    }

    override fun changeExpert(ticketId: Long, expertId: String): Boolean {

        // If the ticketId contained in the URI doesn't exist throws an exception
        val ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()

        // If the expertId contained in the request doesn't exist throws an exception
        val expert = expertRepository.findByIdOrNull((expertId)) ?: throw ExpertNotFoundException()

        // If the ticket is already managed by the expert whose id is contained in the request, just return true
        if (ticket.expert?.id == expertId)
            return true

        // Updates the ticket with the new priority level and saves it.
        ticket.expert = expert
        ticketRepository.save(ticket)

        return true
    }

}