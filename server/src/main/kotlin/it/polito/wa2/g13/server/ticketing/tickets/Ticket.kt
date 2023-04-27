package it.polito.wa2.g13.server.ticketing.tickets


import it.polito.wa2.g13.server.products.Product
import it.polito.wa2.g13.server.profiles.Profile
import it.polito.wa2.g13.server.ticketing.experts.Expert
import jakarta.persistence.*
import java.util.Date

@Entity
@Table(name= "tickets")
class Ticket(
    @Id
    var ticketId: Long,
    @ManyToOne
    @JoinColumn(name = "profileId")
    var profile: Profile,
    @ManyToOne
    @JoinColumn(name = "ean")
    var product: Product,
    var priorityLevel: Int?,
    @ManyToOne
    @JoinColumn(name = "expertId")
    var expert: Expert?,
    var status: String,
    var creationDate: Date
) {

}

fun TicketDTO.toTicket(): Ticket {
    return Ticket(ticketId, profile, product, priorityLevel, expert, status, creationDate)
}
