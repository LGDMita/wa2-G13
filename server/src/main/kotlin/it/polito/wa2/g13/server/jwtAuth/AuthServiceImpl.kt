package it.polito.wa2.g13.server.jwtAuth

import jakarta.transaction.Transactional
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.admin.client.resource.RealmResource
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
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
        } catch (e: NotAuthorizedException) {
            null
        }
    }

    @Transactional
    override fun register(registerDTO: RegisterDTO): String? {
        val keycloak = KeycloakBuilder.builder()
            .serverUrl("http://${keycloakPath}")
            .realm("wa2-g13")
            .clientId("spring-client")
            .username("admin") //in keycloak it is necessary to create a user with 'realm-admin' role
            .password("admin")
            .build()

        val realmResource = keycloak.realm("wa2-g13")

        existsByUsernameOrEmail(realmResource, registerDTO.username, registerDTO.email)

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
        val id = userRepresentation.id
        val userResource = realmResource
            .users()
            .get(id)

        val roleRepresentation = realmResource
            .roles()
            .get("app_client")
            .toRepresentation()

        userResource.roles().realmLevel().add(mutableListOf(roleRepresentation))
        realmResource.users().get(id).update(userResource.toRepresentation())

        return id
    }

    @Transactional
    override fun createExpert(registerDTO: RegisterDTO): String? {
        val keycloak = KeycloakBuilder.builder()
            .serverUrl("http://${keycloakPath}")
            .realm("wa2-g13")
            .clientId("spring-client")
            .username("admin") //in keycloak it is necessary to create a user with 'realm-admin' role
            .password("admin")
            .build()

        val realmResource = keycloak.realm("wa2-g13")

        existsByUsernameOrEmail(realmResource, registerDTO.username, registerDTO.email)

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
        val id = userRepresentation.id
        val userResource = realmResource
            .users()
            .get(id)

        val roleRepresentation = realmResource
            .roles()
            .get("app_expert")
            .toRepresentation()

        userResource.roles().realmLevel().add(mutableListOf(roleRepresentation))
        realmResource.users().get(id).update(userResource.toRepresentation())
        return id
    }

    fun findUserByUsernameAndEmail(
        realmResource: RealmResource,
        username: String,
        email: String
    ): UserRepresentation? {
        val users = realmResource
            .users()
            .search(username)
        return users.firstOrNull { it.email == email }
    }

    fun existsByUsername(
        realmResource: RealmResource,
        username: String
    ): Unit {
        if (realmResource.users().searchByUsername(username, true).size > 0) throw DuplicateUsernameException()
    }

    fun existsByEmail(
        realmResource: RealmResource,
        email: String
    ): Unit {
        if (realmResource.users().searchByEmail(email, true).size > 0) throw DuplicateEmailException()
    }

    fun existsByUsernameOrEmail(
        realmResource: RealmResource,
        username: String,
        email: String
    ): Unit {
        existsByUsername(realmResource, username)
        existsByEmail(realmResource, email)
    }

    @Transactional
    override fun updateUser(id: String, oldRegisterDTO: RegisterDTO, registerDTO: RegisterDTO): Boolean {
        val keycloak = KeycloakBuilder.builder()
            .serverUrl("http://${keycloakPath}")
            .realm("wa2-g13")
            .clientId("spring-client")
            .username("admin") //in keycloak it is necessary to create a user with 'realm-admin' role
            .password("admin")
            .build()

        val realmResource = keycloak.realm("wa2-g13")

        val existingUser = findUserByUsernameAndEmail(realmResource, oldRegisterDTO.username, oldRegisterDTO.email)
            ?: throw UserNotFoundException()

        if (existingUser.email != registerDTO.email) existsByEmail(realmResource, registerDTO.email)

        if (existingUser.username != registerDTO.username) existsByUsername(realmResource, registerDTO.username)

        existingUser.username = registerDTO.username
        existingUser.email = registerDTO.email
        existingUser.firstName = registerDTO.name
        existingUser.lastName = registerDTO.surname

        realmResource
            .users()
            .get(existingUser.id)
            .update(existingUser)

        return true
    }

    @Transactional
    override fun deleteUser(id: String): Unit {
        val keycloak = KeycloakBuilder.builder()
            .serverUrl("http://${keycloakPath}")
            .realm("wa2-g13")
            .clientId("spring-client")
            .username("admin") //in keycloak it is necessary to create a user with 'realm-admin' role
            .password("admin")
            .build()

        val realmResource = keycloak.realm("wa2-g13")

        realmResource.users().delete(id)
    }
}