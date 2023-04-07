package it.polito.wa2.g13.server.profiles

import jakarta.validation.constraints.Pattern
data class ProfileDTO(

    @field:Pattern(regexp = "^[a-z0-9._-]{2,20}[@][a-z]{2,20}([.][a-z]{2,20}?)?[.][a-z]{2,4}\$")  //basic validation for email
    val email: String,
    @field:Pattern(regexp = "^[A-Za-z]+([ ][A-Za-z]+?)*$") //one or more names separated by one space
    val name: String,
    @field:Pattern(regexp = "^[A-Za-z]+([ ][A-Za-z]+?)*$") //one or more surnames separated by one space
    val surname: String
)

fun Profile.toDTO(): ProfileDTO {
    return ProfileDTO(email, name, surname)
}