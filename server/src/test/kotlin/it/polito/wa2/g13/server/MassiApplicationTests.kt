package it.polito.wa2.g13.server

import it.polito.wa2.g13.server.products.Product
import it.polito.wa2.g13.server.products.ProductRepository
import it.polito.wa2.g13.server.profiles.Profile
import it.polito.wa2.g13.server.profiles.ProfileRepository
import it.polito.wa2.g13.server.ticketing.experts.Expert
import it.polito.wa2.g13.server.ticketing.experts.ExpertRepository
import it.polito.wa2.g13.server.ticketing.tickets.Ticket
import it.polito.wa2.g13.server.ticketing.tickets.TicketPostDTO
import it.polito.wa2.g13.server.ticketing.tickets.TicketRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.*
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.net.URI
import java.util.*


@Testcontainers
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
class MassiApplicationTests {
    companion object {
        @Container
        val postgres = PostgreSQLContainer("postgres:latest")
        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") {"create-drop"}
        }
    }
    @LocalServerPort
    protected var port: Int = 0
    @Autowired
    lateinit var restTemplate: TestRestTemplate
    @Autowired
    lateinit var ticketRepository: TicketRepository
    @Autowired
    lateinit var expertRepository: ExpertRepository
    @Autowired
    lateinit var productRepository: ProductRepository
    @Autowired
    lateinit var profileRepository: ProfileRepository

    @Test
    fun creationTicketSuccessTest() {
        productRepository.save(Product(ean = "123123123123123"))
        profileRepository.save(Profile(email = "bob@bob.com", name = "bob", surname = "aggiustatutto"))
        val baseUrl = "http://localhost:$port/API/tickets"
        val uri = URI(baseUrl)
        val ticketPost = TicketPostDTO("bob@bob.com", "123123123123123")

        val headers = HttpHeaders()
        headers.set("X-COM-PERSIST", "true")

        val request = HttpEntity(ticketPost, headers)

        val result = restTemplate.postForEntity(uri, request, String::class.java)

        //Verify request succeed
        Assertions.assertEquals(HttpStatus.CREATED, result.statusCode)

        print(ticketRepository.findById(1).get().product.ean)
    }

    @Test
    fun creationTicketInvalidEanTest() {
        profileRepository.save(Profile(email = "bob@bob.com", name = "bob", surname = "aggiustatutto"))
        val baseUrl = "http://localhost:$port/API/tickets"
        val uri = URI(baseUrl)
        val ticketPost = TicketPostDTO("bob@bob.com", "12312312312312")

        val headers = HttpHeaders()
        headers.set("X-COM-PERSIST", "true")

        val request = HttpEntity(ticketPost, headers)

        val result = restTemplate.postForEntity(uri, request, String::class.java)

        //Verify request succeed
        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result.statusCode)
    }

    @Test
    fun creationTicketBlankProfileIdTest() {
        productRepository.save(Product(ean = "123123123123123"))
        val baseUrl = "http://localhost:$port/API/tickets"
        val uri = URI(baseUrl)
        val ticketPost = TicketPostDTO("", "12312312312312")

        val headers = HttpHeaders()
        headers.set("X-COM-PERSIST", "true")

        val request = HttpEntity(ticketPost, headers)

        val result = restTemplate.postForEntity(uri, request, String::class.java)

        //Verify request succeed
        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result.statusCode)
    }

    @Test
    fun creationTicketNonExistingProfileIdTest() {
        productRepository.save(Product(ean = "123123123123123"))
        val baseUrl = "http://localhost:$port/API/tickets"
        val uri = URI(baseUrl)
        val ticketPost = TicketPostDTO("bob@bob.com", "123123123123123")

        val headers = HttpHeaders()
        headers.set("X-COM-PERSIST", "true")

        val request = HttpEntity(ticketPost, headers)

        val result = restTemplate.postForEntity(uri, request, String::class.java)

        //Verify request succeed
        Assertions.assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
    }

    @Test
    fun creationTicketNonExistingEanTest() {
        profileRepository.save(Profile(email = "bob@bob.com", name = "bob", surname = "aggiustatutto"))
        val baseUrl = "http://localhost:$port/API/tickets"
        val uri = URI(baseUrl)
        val ticketPost = TicketPostDTO("bob@bob.com", "123123123123123")

        val headers = HttpHeaders()
        headers.set("X-COM-PERSIST", "true")

        val request = HttpEntity(ticketPost, headers)

        val result = restTemplate.postForEntity(uri, request, String::class.java)

        //Verify request succeed
        Assertions.assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
    }

    @Test
    fun updateTicketStatusSuccessTest() {
        val product = Product(ean = "123123123123123")
        val profile = Profile(email = "bob@bob.com", name = "bob", surname = "aggiustatutto")
        productRepository.save(product)
        profileRepository.save(profile)
        ticketRepository.save(Ticket(creationDate = Date(), expert = null, priorityLevel = null, product = product, profile = profile, status = "open"))

        val baseUrl = "http://localhost:$port/API/tickets/1/changeStatus"
        val uri = URI(baseUrl)
        val ticketPut = mapOf("status" to "in_progress")

        val headers = HttpHeaders()
        headers.set("X-COM-PERSIST", "true")

        val request = HttpEntity(ticketPut, headers)

        val result = restTemplate.exchange(uri, HttpMethod.PUT, request, String::class.java)

        //Verify request succeed
        Assertions.assertEquals(HttpStatus.OK, result.statusCode)

        print(ticketRepository.findById(1).get().status)
    }

    @Test
    fun updateTicketPrioritySuccessTest() {
        val product = Product(ean = "123123123123123")
        val profile = Profile(email = "bob@bob.com", name = "bob", surname = "aggiustatutto")
        productRepository.save(product)
        profileRepository.save(profile)
        ticketRepository.save(Ticket(creationDate = Date(), expert = null, priorityLevel = null, product = product, profile = profile, status = "open"))

        val baseUrl = "http://localhost:$port/API/tickets/1/changePriority"
        val uri = URI(baseUrl)
        val ticketPut = mapOf("priorityLevel" to 1)

        val headers = HttpHeaders()
        headers.set("X-COM-PERSIST", "true")

        val request = HttpEntity(ticketPut, headers)

        val result = restTemplate.exchange(uri, HttpMethod.PUT, request, String::class.java)

        //Verify request succeed
        Assertions.assertEquals(HttpStatus.OK, result.statusCode)

        print(ticketRepository.findById(1).get().priorityLevel)
    }

    @Test
    fun updateTicketExpertSuccessTest() {
        val product = Product(ean = "123123123123123")
        val profile = Profile(email = "bob@bob.com", name = "bob", surname = "aggiustatutto")
        productRepository.save(product)
        profileRepository.save(profile)
        expertRepository.save(Expert(email = "tom@tom.com", name = "timmy", surname = "tom"))
        ticketRepository.save(Ticket(creationDate = Date(), expert = null, priorityLevel = null, product = product, profile = profile, status = "open"))

        val baseUrl = "http://localhost:$port/API/tickets/1/changeExpert"
        val uri = URI(baseUrl)
        val ticketPost = mapOf("expertId" to 1)

        val headers = HttpHeaders()
        headers.set("X-COM-PERSIST", "true")

        val request = HttpEntity(ticketPost, headers)

        val result = restTemplate.exchange(uri, HttpMethod.PUT, request, String::class.java)

        //Verify request succeed
        Assertions.assertEquals(HttpStatus.OK, result.statusCode)

        print(ticketRepository.findById(1).get().expert?.getId())
    }
}
