package it.polito.wa2.g13.server.jwtAuth

import it.polito.wa2.g13.server.profiles.*
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.status

@Service
class AuthServiceImpl : AuthService {

    @Value("\${keycloak.address}")
    private lateinit var keycloakPath: String

    override fun login(loginDTO: LoginDTO): JwtResponse {
        val keycloak = KeycloakBuilder.builder()
            .serverUrl("http://${keycloakPath}") // Replace with your Keycloak server URL
            .realm("wa2-g13") // Replace with your realm name
            .clientId("spring-client") // Replace with your client ID
            .username(loginDTO.username) // Replace with the username of the user to authenticate
            .password(loginDTO.password) // Replace with the password of the user to authenticate
            .build()

        return JwtResponse(keycloak.tokenManager().grantToken().token)
    }

    override fun register(registerDTO: RegisterDTO): Response {
        val keycloak = KeycloakBuilder.builder()
            .serverUrl("http://${keycloakPath}") // Replace with your Keycloak server URL
            .realm("wa2-g13") // Replace with your realm name
            .clientId("spring-client")
            .username("admin") // Replace with your admin username
            .password("admin") // Replace with your admin password
            .build()

        val existingUser = findUserByUsernameOrEmail(keycloak, registerDTO.username, registerDTO.email)
        if (existingUser != null) {
            return status(Response.Status.CONFLICT)
                .entity("User with the same username or email already exists.")
                .build()
        }

        val user = UserRepresentation()
        user.username = registerDTO.username
        user.email = registerDTO.email
        user.firstName = registerDTO.name
        user.lastName = registerDTO.surname
        user.isEnabled = true
        user.isEmailVerified = true

        val credential = CredentialRepresentation()
        credential.isTemporary = false
        credential.type = CredentialRepresentation.PASSWORD
        credential.value = registerDTO.password

        user.credentials = listOf(credential)

        keycloak.realm("wa2-g13") // Replace with your realm name
            .users()
            .create(user)

        return status(Response.Status.CREATED)
            .entity("User successfully created")
            .build()
    }

    fun findUserByUsernameOrEmail(
        keycloak: Keycloak,
        username: String,
        email: String
    ): UserRepresentation? {
        val users = keycloak.realm("wa2-g13") // Replace with your realm name
            .users()
            .search(username)

        return users.firstOrNull { it.email == email }
    }
}