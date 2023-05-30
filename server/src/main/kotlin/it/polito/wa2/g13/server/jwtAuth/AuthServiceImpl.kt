package it.polito.wa2.g13.server.jwtAuth

import it.polito.wa2.g13.server.profiles.DuplicateProfileException
import it.polito.wa2.g13.server.profiles.ProfileDTO
import it.polito.wa2.g13.server.profiles.ProfileService
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.admin.client.resource.RealmResource
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

    override fun register(registerDTO: RegisterDTO): String? {
        val keycloak = KeycloakBuilder.builder()
            .serverUrl("http://${keycloakPath}")
            .realm("wa2-g13")
            .clientId("spring-client")
            .username("admin") //in keycloak it is necessary to create a user with 'realm-admin' role
            .password("admin")
            .build()

        val realmResource= keycloak.realm("wa2-g13")

        val existingUser = findUserByUsernameOrEmail(realmResource, registerDTO.username, registerDTO.email)
        if (existingUser != null) {
            return null
        }

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
        val id= userRepresentation.id
        val userResource= realmResource
            .users()
            .get(id)

        val roleRepresentation= realmResource
            .roles()
            .get("app_client")
            .toRepresentation()

        userResource.roles().realmLevel().add(mutableListOf(roleRepresentation))
        realmResource.users().get(id).update(userResource.toRepresentation())

        return id
    }

    override fun createExpert(registerDTO: RegisterDTO): String? {
        val keycloak = KeycloakBuilder.builder()
            .serverUrl("http://${keycloakPath}")
            .realm("wa2-g13")
            .clientId("spring-client")
            .username("admin") //in keycloak it is necessary to create a user with 'realm-admin' role
            .password("admin")
            .build()

        val realmResource= keycloak.realm("wa2-g13")

        val existingUser = findUserByUsernameOrEmail(realmResource, registerDTO.username, registerDTO.email)
        if (existingUser != null) {
            return null
        }

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
        //user as "app_expert"
        val userRepresentation = realmResource
            .users()
            .search(newUser.username)
            .first()
        val id= userRepresentation.id
        val userResource= realmResource
            .users()
            .get(id)

        val roleRepresentation= realmResource
            .roles()
            .get("app_expert")
            .toRepresentation()

        userResource.roles().realmLevel().add(mutableListOf(roleRepresentation))
        realmResource.users().get(id).update(userResource.toRepresentation())

        return id
    }

    fun findUserByUsernameOrEmail(
        realmResource: RealmResource,
        username: String,
        email: String
    ): UserRepresentation? {
        val users = realmResource
            .users()
            .search(username)

        return users.firstOrNull { it.email == email }
    }
}