package it.polito.wa2.g13.server

import it.polito.wa2.g13.server.jwtAuth.AuthController
import it.polito.wa2.g13.server.jwtAuth.LoginDTO
import it.polito.wa2.g13.server.products.Product
import it.polito.wa2.g13.server.products.ProductRepository
import it.polito.wa2.g13.server.profiles.Profile
import it.polito.wa2.g13.server.profiles.ProfileRepository
import it.polito.wa2.g13.server.ticketing.experts.Expert
import it.polito.wa2.g13.server.ticketing.experts.ExpertRepository
import it.polito.wa2.g13.server.ticketing.tickets.Ticket
import it.polito.wa2.g13.server.ticketing.tickets.TicketRepository
import it.polito.wa2.g13.server.ticketing.ticketsHistory.TicketHistory
import it.polito.wa2.g13.server.ticketing.ticketsHistory.TicketHistoryRepository
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
class TicketHistoryTests {
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
    lateinit var profileRepository: ProfileRepository

    @Autowired
    lateinit var productRepository: ProductRepository

    @Autowired
    lateinit var expertRepository: ExpertRepository

    @Autowired
    lateinit var ticketRepository: TicketRepository


    @Autowired
    lateinit var ticketHistoryRepository: TicketHistoryRepository

    @Autowired
    lateinit var authController: AuthController

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun t1TestGetTicketHistory(){
        val baseUrl = "http://localhost:$port/API/tickets/1/history"
        val uri = URI(baseUrl)
        val myProfile = Profile("moyne@gmail.com", "Mohamed Amine", "Hamdi")
        val myProduct = Product(
            "4935531465706",
            "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
            "JMT"
        )
        val myExpert = Expert("Giovanni", "Malnati", "giovanni.malnati@polito.it")
        val myTicket = Ticket(
            profile = myProfile, product = myProduct, priorityLevel = 1, expert = myExpert,
            status = "open", creationDate = Date(), messages = mutableSetOf()
        )
        val myHistory= TicketHistory(myTicket,"OPEN","CLOSED",Date())

        profileRepository.save(myProfile)
        productRepository.save(myProduct)
        expertRepository.save(myExpert)
        ticketRepository.save(myTicket)
        ticketHistoryRepository.save(myHistory)

        val loginDTO= LoginDTO(username = "expert", password = "password")
        val token= authController.login(loginDTO).jwtAccessToken
        val headers = HttpHeaders()
        headers.setBearerAuth(token)
        headers.set("X-COM-PERSIST", "true")
        val request = HttpEntity(null, headers)
        val result = restTemplate.exchange(uri, HttpMethod.GET, request, String::class.java)


        Assertions.assertEquals(HttpStatus.OK, result.statusCode)

        ticketHistoryRepository.delete(myHistory)
        ticketRepository.delete(myTicket)
        profileRepository.delete(myProfile)
        productRepository.delete(myProduct)
        expertRepository.delete(myExpert)
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun t2TestGetHistoryOfNotExistentTicket(){
        val baseUrl = "http://localhost:$port/API/tickets/1000/history"
        val uri = URI(baseUrl)

        val loginDTO= LoginDTO(username = "expert", password = "password")
        val token= authController.login(loginDTO).jwtAccessToken
        val headers = HttpHeaders()
        headers.setBearerAuth(token)
        headers.set("X-COM-PERSIST", "true")
        val request = HttpEntity(null, headers)
        val result = restTemplate.exchange(uri, HttpMethod.GET, request, String::class.java)

        Assertions.assertEquals(result.statusCode,HttpStatus.NOT_FOUND)
    }
}