package it.polito.wa2.g13.server.ticketing.experts

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ExpertDTO(
    val expertId: Long,
    @field:Size(min=1, max=255, message = "Name MUST be a NON empty string of max 255 chars")
    @field:NotBlank(message="Name can NOT be blank")
    val name: String,
    @field:Size(min=1, max=255, message = "Surname MUST be a NON empty string of max 255 chars")
    @field:NotBlank(message="Name can NOT be blank")
    val surname: String,
    @field:Email(message="The email MUST be in a valid email format ")
    @field:NotBlank(message="Email can NOT be blank")
    val email: String
)

fun Expert.toDTO(): ExpertDTO {
    return ExpertDTO(getId(), name, surname, sector, email)
}