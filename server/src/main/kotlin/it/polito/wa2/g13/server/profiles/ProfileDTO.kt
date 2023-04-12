package it.polito.wa2.g13.server.profiles

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class ProfileDTO(

    @field:Email
    @field:NotNull
    @field:NotBlank
    val email: String,
    @field:Size(min=1, max=255)
    @field:NotNull
    @field:NotBlank
    val name: String,
    @field:Size(min=1, max=255)
    @field:NotNull
    @field:NotBlank
    val surname: String
)

fun Profile.toDTO(): ProfileDTO {
    return ProfileDTO(email, name, surname)
}