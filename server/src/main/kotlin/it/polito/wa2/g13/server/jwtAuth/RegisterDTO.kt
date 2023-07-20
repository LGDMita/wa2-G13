package it.polito.wa2.g13.server.jwtAuth

import jakarta.validation.constraints.*

data class RegisterDTO(
    @field:Size(min = 1, max = 255, message = "Username MUST be a NON empty string of max 255 chars")
    @field:NotBlank(message = "Username can NOT be blank")
    var username: String,
    @field:Size(min = 8, max = 32, message = "Password MUST be a NON empty string of min 8 and max 32 chars")
    @field:NotBlank(message = "Password can NOT be blank")
    var password: String,
    @field:Email(message = "The email MUST be in a valid email format ")
    @field:NotBlank(message = "Email can NOT be blank")
    var email: String,
    @field:Size(min = 1, max = 255, message = "Name MUST be a NON empty string of max 255 chars")
    @field:NotBlank(message = "Name can NOT be blank")
    var name: String,
    @field:Size(min = 1, max = 255, message = "Surname MUST be a NON empty string of max 255 chars")
    @field:NotBlank(message = "Surname can NOT be blank")
    var surname: String
) {
    override fun toString(): String {
        return "Username=${username}&Password=${password}&Email=${email}&Name=${name}&Surname=${surname}"
    }
}