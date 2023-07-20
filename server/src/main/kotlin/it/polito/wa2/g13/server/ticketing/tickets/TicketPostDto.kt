package it.polito.wa2.g13.server.ticketing.tickets

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class TicketPostDTO(
    @field:Size(min = 13, max = 13, message = "The inserted input is not valid!")
    var ean: String
) {
    override fun toString(): String {
        return "EAN=$ean"
    }
}