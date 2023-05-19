package it.polito.wa2.g13.server.jwtAuth

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import it.polito.wa2.g13.server.profiles.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

@Service
class AuthServiceImpl() :AuthService {

    @Value("\${keycloak.address}")
    private lateinit var keycloak: String

    override fun login(loginDTO: LoginDTO): JwtResponse {
        val url = "http://${keycloak}/realms/wa2-g13/protocol/openid-connect/token"
        val restTemplate = RestTemplate()

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val body: MultiValueMap<String, String> = LinkedMultiValueMap()
        body.add("grant_type", "password")
        body.add("client_id", "spring-client")
        body.add("username", loginDTO.username)
        body.add("password", loginDTO.password)

        val requestEntity = org.springframework.http.HttpEntity(body, headers)
        val responseEntity: ResponseEntity<String>
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String::class.java)
        } catch (e: Exception) {
            throw InvalidCredentialArgumentsException()
        }

        val response = responseEntity.body!!

        // Parse the JSON response
        val objectMapper = ObjectMapper()
        val jsonResponse: AccessTokenResponse = objectMapper.readValue(response)

        /*// Access the parsed JSON data
        println("Access Token: ${jsonResponse.access_token}")
        println("Expires In: ${jsonResponse.expires_in}")
        println("Refresh Token: ${jsonResponse.refresh_token}")*/

        return JwtResponse(jsonResponse.access_token)
    }
}

data class AccessTokenResponse @JsonCreator constructor(
    @JsonProperty("access_token") val access_token: String,
    @JsonProperty("expires_in") val expires_in: Int,
    @JsonProperty("refresh_expires_in") val refresh_expires_in: Int,
    @JsonProperty("refresh_token") val refresh_token: String,
    @JsonProperty("token_type") val token_type: String,
    @JsonProperty("not-before-policy") val not_before_policy: Int,
    @JsonProperty("session_state") val session_state: String,
    @JsonProperty("scope") val scope: String
) {
    @JsonCreator
    constructor() : this("", 0, 0, "", "", 0, "", "")
}