package it.polito.wa2.g13.server.ticketing.tickets


import it.polito.wa2.g13.server.products.Product
import it.polito.wa2.g13.server.profiles.Profile
import it.polito.wa2.g13.server.ticketing.experts.Expert
import it.polito.wa2.g13.server.ticketing.messages.Message
import jakarta.persistence.*
import java.util.Date

@Entity
@Table(name= "tickets")
class Ticket(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tickets_generator")
    @SequenceGenerator(name = "tickets_generator", sequenceName = "tickets_seq", allocationSize = 1)
    var ticketId: Long = 0,
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
    var creationDate: Date,
    @OneToMany(mappedBy = "ticket")
    var messages : MutableSet<Message>? = mutableSetOf()
) {
    fun addMessage(message: Message){
        message.ticket=this
        messages?.add(message)
    }
}

fun TicketDTO.toTicket(): Ticket {
    return Ticket(ticketId, profile, product, priorityLevel, expert, status, creationDate, messages)
}
