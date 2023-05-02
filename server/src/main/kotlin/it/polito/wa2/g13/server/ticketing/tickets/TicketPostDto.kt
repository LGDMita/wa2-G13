package it.polito.wa2.g13.server.ticketing.tickets

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.*

data class TicketPostDTO(
    @field:NotBlank
    var profileId: String,
    @field:Size(min=15, max=15)
    var ean: String
)