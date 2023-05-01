package it.polito.wa2.g13.server.ticketing.tickets


import it.polito.wa2.g13.server.profiles.Profile
import it.polito.wa2.g13.server.ticketing.experts.Expert
import it.polito.wa2.g13.server.ticketing.messages.Message
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "tickets")
class Ticket(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tickets_generator")
    @SequenceGenerator(name = "tickets_generator", sequenceName = "tickets_seq", allocationSize = 1)
    var ticketId: Long,
    @ManyToOne
    @JoinColumn(name = "profileId")
    var profile: Profile,
    var ean: String,
    var priorityLevel: Int,
    @ManyToOne
    @JoinColumn(name = "expertId")
    var expert: Expert,
    var status: String,
    var creationDate: Date,
    @OneToMany(mappedBy = "ticket", cascade = [CascadeType.ALL])
    var messages: MutableSet<Message>
)

fun TicketDTO.toTicket(): Ticket {
    return Ticket(ticketId, profile, ean, priorityLevel, expert, status, creationDate, messages)
}
