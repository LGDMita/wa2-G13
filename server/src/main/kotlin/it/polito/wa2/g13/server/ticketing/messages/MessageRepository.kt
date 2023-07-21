package it.polito.wa2.g13.server.ticketing.messages

import it.polito.wa2.g13.server.ticketing.tickets.Ticket
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository : JpaRepository<Message, String> {

    fun findByTicket(ticket: Ticket): List<Message>
}