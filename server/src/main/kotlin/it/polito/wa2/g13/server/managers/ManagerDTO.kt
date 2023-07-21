package it.polito.wa2.g13.server.managers

import it.polito.wa2.g13.server.jwtAuth.RegisterDTO
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ManagerDTO(
    @field:NotBlank(message = "Id can NOT be blank")
    val id: String,
    @field:Size(min = 1, max = 255, message = "Username MUST be a NON empty string of max 255 chars")
    @field:NotBlank(message = "Username can NOT be blank")
    val username: String,
    @field:Email(message = "The email MUST be in a valid email format ")
    @field:NotBlank(message = "Email can NOT be blank")
    val email: String,
    @field:Size(min = 1, max = 255, message = "Name MUST be a NON empty string of max 255 chars")
    @field:NotBlank(message = "Name can NOT be blank")
    val name: String,
    @field:Size(min = 1, max = 255, message = "Surname MUST be a NON empty string of max 255 chars")
    @field:NotBlank(message = "Surname can NOT be blank")
    val surname: String
) {
    override fun toString(): String {
        return "Id=$id&Username=$username&Email=$email&Name=$name&Surname=$surname"
    }
}

fun Manager.toDTO(): ManagerDTO {
    return ManagerDTO(id, username, email, name, surname)
}

fun ManagerDTO.toRegisterDTO(): RegisterDTO {
    return RegisterDTO(username, "", email, name, surname)
}
