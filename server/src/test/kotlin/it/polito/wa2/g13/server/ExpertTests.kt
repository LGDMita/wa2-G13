package it.polito.wa2.g13.server

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import it.polito.wa2.g13.server.jwtAuth.AuthController
import it.polito.wa2.g13.server.jwtAuth.LoginDTO
import it.polito.wa2.g13.server.ticketing.experts.Expert
import it.polito.wa2.g13.server.ticketing.experts.ExpertDTO
import it.polito.wa2.g13.server.ticketing.experts.ExpertRepository
import org.junit.FixMethodOrder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.*
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.net.URI
import java.util.*


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) // force name ordering
class ExpertTests {
    companion object {
        @Container
        val postgres = PostgreSQLContainer("postgres:latest")

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
        }
    }

    @LocalServerPort
    protected var port: Int = 8080

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var expertRepository: ExpertRepository

    @Autowired
    lateinit var authController: AuthController

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun t1TestGetExperts(){
        val baseUrl = "http://localhost:$port/API/experts"
        val uri = URI(baseUrl)
        val myExpert = Expert("Giovanni", "Malnati", "giovanni.malnati@polito.it")

        expertRepository.save(myExpert)


        val loginDTO= LoginDTO(username = "expert", password = "password")
        val token= authController.login(loginDTO).jwtAccessToken
        val headers = HttpHeaders()
        headers.setBearerAuth(token)
        headers.set("X-COM-PERSIST", "true")
        val request = HttpEntity(null, headers)
        val result = restTemplate.exchange(uri, HttpMethod.GET, request, String::class.java)


        Assertions.assertEquals(HttpStatus.OK, result.statusCode)

        expertRepository.delete(myExpert)
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun t1BTestGetExperts(){
        val baseUrl = "http://localhost:$port/API/experts"
        val uri = URI(baseUrl)
        val myExpert = Expert("Giovanni", "Malnati", "giovanni.malnati@polito.it")

        expertRepository.save(myExpert)


        val loginDTO= LoginDTO(username = "client", password = "password")
        val token= authController.login(loginDTO).jwtAccessToken
        val headers = HttpHeaders()
        headers.setBearerAuth(token)
        headers.set("X-COM-PERSIST", "true")
        val request = HttpEntity(null, headers)
        val result = restTemplate.exchange(uri, HttpMethod.GET, request, String::class.java)


        Assertions.assertEquals(HttpStatus.FORBIDDEN, result.statusCode)

        expertRepository.delete(myExpert)
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun t2TestGetExpertsMultiple(){
        val baseUrl = "http://localhost:$port/API/experts"
        val uri = URI(baseUrl)
        val myExpert = Expert("Giovanni", "Malnati", "giovanni.malnati@polito.it")
        val myExpert2= Expert("Piero","Lelu","pieroLelu@gmail.com")
        expertRepository.save(myExpert)
        expertRepository.save(myExpert2)


        val loginDTO= LoginDTO(username = "expert", password = "password")
        val token= authController.login(loginDTO).jwtAccessToken
        val headers = HttpHeaders()
        headers.setBearerAuth(token)
        headers.set("X-COM-PERSIST", "true")
        val request = HttpEntity(null, headers)
        val result = restTemplate.exchange(uri, HttpMethod.GET, request, String::class.java)


        val gson = Gson()
        val arrayExpertType = object : TypeToken<List<Expert>>() {}.type
        val experts: List<ExpertDTO> = gson.fromJson(result.body, arrayExpertType)

        Assertions.assertEquals(HttpStatus.OK, result.statusCode)
        Assertions.assertEquals(experts.size,2)

        expertRepository.delete(myExpert)
        expertRepository.delete(myExpert2)
    }

}