package it.polito.wa2.g13.server.ticketing.messages

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository : JpaRepository<Message, String> {
}