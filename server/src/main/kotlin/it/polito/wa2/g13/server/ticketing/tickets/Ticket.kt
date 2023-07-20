package it.polito.wa2.g13.server.ticketing.tickets


import it.polito.wa2.g13.server.EntityBase
import it.polito.wa2.g13.server.products.Product
import it.polito.wa2.g13.server.products.toProduct
import it.polito.wa2.g13.server.profiles.Profile
import it.polito.wa2.g13.server.profiles.toProfile
import it.polito.wa2.g13.server.ticketing.experts.Expert
import it.polito.wa2.g13.server.ticketing.experts.toExpert
import it.polito.wa2.g13.server.ticketing.messages.Message
import it.polito.wa2.g13.server.ticketing.messages.toMessage
import jakarta.persistence.*
import java.util.Date

@Entity
@Table(name = "tickets")
class Ticket(
    @ManyToOne
    @JoinColumn(name = "profile_id")
    var profile: Profile,
    @ManyToOne
    @JoinColumn(name = "ean")
    var product: Product,
    var priorityLevel: Int?,
    @ManyToOne
    @JoinColumn(name = "expert_id")
    var expert: Expert?,
    var status: String,
    var creationDate: Date,
    @OneToMany(mappedBy = "ticket")
    var messages: MutableSet<Message> = mutableSetOf(),
    setId: Long? = null
) : EntityBase<Long>(setId) {
    fun addMessage(message: Message) {
        message.ticket = this
        messages.add(message)
    }
}

fun TicketDTO.toTicket(): Ticket {
    val tick = Ticket(
        profile.toProfile(), product.toProduct(), priorityLevel, expert?.toExpert(), status,
        creationDate, mutableSetOf(), ticketId
    );
    tick.messages = messages.map { it.toMessage(tick) }.toMutableSet();
    return tick;
}
