package it.polito.wa2.g13.server.ticketing.tickets

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class TicketPostDTO(
    @field:NotBlank(message = "The inserted input is not valid!")
    var email: String,
    @field:Size(min=13, max=13, message = "The inserted input is not valid!")
    var ean: String
) {
    override fun toString(): String {
        return "Email=$email&EAN=$ean"
    }
}