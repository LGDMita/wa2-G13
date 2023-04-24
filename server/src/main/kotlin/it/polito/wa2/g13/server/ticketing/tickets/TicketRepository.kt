package it.polito.wa2.g13.server.ticketing.tickets

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TicketRepository : JpaRepository<Ticket, String> {
}