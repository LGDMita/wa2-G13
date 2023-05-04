package it.polito.wa2.g13.server.ticketing.tickets

import it.polito.wa2.g13.server.products.ProductDTO
import it.polito.wa2.g13.server.products.toDTO
import it.polito.wa2.g13.server.profiles.ProfileDTO
import it.polito.wa2.g13.server.profiles.toDTO
import it.polito.wa2.g13.server.ticketing.experts.ExpertDTO
import it.polito.wa2.g13.server.ticketing.experts.toDTO
import it.polito.wa2.g13.server.ticketing.messages.MessageDTO
import it.polito.wa2.g13.server.ticketing.messages.toDTO
import jakarta.validation.Valid
import jakarta.validation.constraints.*

import java.util.*

data class TicketDTO(
    @field:NotNull
    var ticketId: Long,
    @Valid
    var profile: ProfileDTO,
    @Valid
    var product: ProductDTO,
    @field:Min(value = 0, message = "Minimum value for priorityLevel is 0")
    @field:Max(value = 4, message = "Minimum value for priorityLevel is 4")
    var priorityLevel: Int?,
    @Valid
    var expert: ExpertDTO?,
    @field:Size(min=1, max=15, message = "Status MUST be a NON empty string of max 15 chars")
    @field:NotBlank(message="Status can NOT be blank")
    @field:Pattern(regexp = "(open|closed|resolved|in_progress|reopened)")
    var status: String,
    @field:NotNull
    var creationDate: Date,
    var messages: MutableSet<MessageDTO>
)

fun Ticket.toDTO(): TicketDTO {
    return TicketDTO(ticketId, profile.toDTO(), product.toDTO(), priorityLevel, expert?.toDTO(), status, creationDate,
        messages.map { m -> m.toDTO() }.toMutableSet())
}