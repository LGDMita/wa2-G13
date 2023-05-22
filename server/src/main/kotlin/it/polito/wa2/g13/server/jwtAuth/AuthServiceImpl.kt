package it.polito.wa2.g13.server.jwtAuth

import it.polito.wa2.g13.server.profiles.DuplicateProfileException
import it.polito.wa2.g13.server.profiles.ProfileDTO
import it.polito.wa2.g13.server.profiles.ProfileService
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.status
import javax.ws.rs.NotAuthorizedException


@Service
class AuthServiceImpl : AuthService {

    @Value("\${keycloak.address}")
    private lateinit var keycloakPath: String

    private lateinit var profileService: ProfileService

    override fun login(loginDTO: LoginDTO): JwtResponse? {

        val keycloak = KeycloakBuilder.builder()
            .serverUrl("http://${keycloakPath}")
            .realm("wa2-g13")
            .clientId("spring-client")
            .username(loginDTO.username)
            .password(loginDTO.password)
            .build()

        return try {
            JwtResponse(keycloak.tokenManager().grantToken().token)
        }catch(e: NotAuthorizedException){
            null
        }
    }

    override fun register(registerDTO: RegisterDTO): Response? {
        val keycloak = KeycloakBuilder.builder()
            .serverUrl("http://${keycloakPath}")
            .realm("wa2-g13")
            .clientId("spring-client")
            .username("admin")
            .password("admin")
            .build()

        val existingUser = findUserByUsernameOrEmail(keycloak, registerDTO.username, registerDTO.email)
        if (existingUser != null) {
            throw DuplicateProfileException()
        }
        val realmResource= keycloak.realm("wa2-g13")

        val newUser = UserRepresentation()
        newUser.username = registerDTO.username
        newUser.email = registerDTO.email
        newUser.firstName = registerDTO.name
        newUser.lastName = registerDTO.surname
        newUser.isEnabled = true
        newUser.isEmailVerified = true

        val credential = CredentialRepresentation()
        credential.isTemporary = false
        credential.type = CredentialRepresentation.PASSWORD
        credential.value = registerDTO.password

        newUser.credentials = listOf(credential)

       realmResource
            .users()
            .create(newUser)

        //In the following, several steps to set the role of the created
        //user as "app_client"
        val userRepresentation = realmResource
            .users()
            .search(newUser.username)
            .first()
        val userResource= realmResource
            .users()
            .get(userRepresentation.id)

        val roleRepresentation= realmResource
            .roles()
            .get("app_client")
            .toRepresentation()

        userResource.roles().realmLevel().add(mutableListOf(roleRepresentation))
        realmResource.users().get(userRepresentation.id).update(userResource.toRepresentation())

        if (profileService.saveNewProfile(ProfileDTO(userRepresentation.id, registerDTO.username, registerDTO.email,
                registerDTO.name, registerDTO.surname))) {
            return status(Response.Status.CREATED)
                .entity("User successfully created")
                .build()
        }
        else {
            keycloak.realm("wa2-g13").users().delete(userRepresentation.id)
            throw ImpossibleSaveNewUserException()
        }
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