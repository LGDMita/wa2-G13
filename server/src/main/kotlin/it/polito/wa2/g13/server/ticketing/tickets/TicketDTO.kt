package it.polito.wa2.g13.server.ticketing.tickets

import it.polito.wa2.g13.server.profiles.Profile
import it.polito.wa2.g13.server.ticketing.experts.Expert
import it.polito.wa2.g13.server.ticketing.messages.Message
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

import java.util.*

data class TicketDTO(
    @field:NotNull
    var ticketId: Long,
    @Valid
    var profile: Profile,
    @field:Size(min=1, max=15, message = "Ean MUST be a NON empty string of max 15 chars")
    @field:NotBlank(message="Ean can NOT be blank")
    var ean: String,
    @field:Min(value = 0, message = "Minimum value for priorityLevel is 0")
    @field:Max(value = 4, message = "Minimum value for priorityLevel is 4")
    var priorityLevel: Int,
    @Valid
    var expert: Expert,
    @field:Size(min=1, max=15, message = "Status MUST be a NON empty string of max 15 chars")
    @field:NotBlank(message="Status can NOT be blank")
    var status: String,
    @field:NotNull
    var creationDate: Date,
    var messages: MutableSet<Message>
)

fun Ticket.toDTO(): TicketDTO {
    return TicketDTO(ticketId, profile, ean, priorityLevel, expert, status, creationDate, messages)
}