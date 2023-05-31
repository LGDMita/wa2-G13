package it.polito.wa2.g13.server.ticketing.tickets

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TicketRepository : JpaRepository<Ticket, Long> {

   @Query(
        "Select t from Ticket t where (:ean is null or t.product.ean=:ean) " +
                "and (:profileId is null or t.profile.email=:profileId) " +
                "and (:priorityLevel is null or t.priorityLevel=:priorityLevel) " +
                "and (:expertId is null or t.expert.id=:expertId) and (:status is null or t.status=:status) " +
                "and (cast(:creationDateStart as timestamp) is null or t.creationDate >= :creationDateStart) " +
                "and (cast(:creationDateStop as timestamp) is null or t.creationDate <= :creationDateStop)"
    )
    fun getFilteredTickets(
        @Param("ean") ean: String?, @Param("profileId") profileId: String?,
        @Param("priorityLevel") priorityLevel: Int?, @Param("expertId") expertId: String?,
        @Param("status") status: String?, @Param("creationDateStart") creationDateStart: Date?,
        @Param("creationDateStop") creationDateStop: Date?
    ): List<Ticket>
}