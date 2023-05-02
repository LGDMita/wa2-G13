package it.polito.wa2.g13.server.ticketing.messages

import it.polito.wa2.g13.server.ticketing.attachments.Attachment
import it.polito.wa2.g13.server.ticketing.attachments.toAttachment
import it.polito.wa2.g13.server.ticketing.tickets.Ticket
import it.polito.wa2.g13.server.ticketing.tickets.TicketDTO
import it.polito.wa2.g13.server.ticketing.tickets.toTicket
import jakarta.persistence.*
import java.util.Date

@Entity
@Table(name= "messages")
class Message (
    @ManyToOne
    var ticket: Ticket? = null,
    var fromUser: Boolean,
    var text: String,
    var datetime: Date,
    @OneToMany(mappedBy = "message", cascade = [CascadeType.ALL])
    var attachments: MutableSet<Attachment>,
    setId: Long?=null
) : EntityBase<Long>(setId) {
    init {
        attachments.forEach{it.message=this}
    }
}

fun MessageDTO.toMessage(ticket: TicketDTO?): Message {
    val mex=Message(ticket?.toTicket(),fromUser, text, datetime, mutableSetOf() ,messageId)
    mex.attachments=attachments.map{it.toAttachment(mex)}.toMutableSet()
    return mex
}
