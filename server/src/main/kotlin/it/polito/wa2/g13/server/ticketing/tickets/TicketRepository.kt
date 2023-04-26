package it.polito.wa2.g13.server.ticketing.tickets

import it.polito.wa2.g13.server.profiles.Profile
import it.polito.wa2.g13.server.ticketing.experts.Expert
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TicketRepository : JpaRepository<Ticket, String> {

    @Query(
        "Select t from Ticket t where (:ean is null or t.ean=:ean) and (:profile is null or t.profile=:profile)" +
                "and (:priorityLevel is null or t.priorityLevel=:priorityLevel) and (:expert is null or " +
                "t.expert=:expert) and (:status is null or t.status=:status)and (:creationDate is null or " +
                "t.creationDate=:creationDate)"
    )
    fun filterTickets(
        @Param("ean") ean: String, @Param("profile") profile: Profile,
        @Param("priorityLevel") priorityLevel: Int, @Param("expert") expert: Expert,
        @Param("status") status: String, @Param("creationDate") creationDate: Date
    ): List<Ticket>
}