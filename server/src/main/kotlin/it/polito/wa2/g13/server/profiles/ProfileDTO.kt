package it.polito.wa2.g13.server.profiles

data class ProfileDTO(
    val email : String,
    val name : String,
    val surname : String
)

fun Profile.toDTO() : ProfileDTO{
    return ProfileDTO(email, name, surname)
}