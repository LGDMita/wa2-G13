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

    fun creationTicketTest(
        baseUrl: String = "http://localhost:$port/API/tickets",
        profileId: String = "this@exists.com",
        ean: String = "000000000000000",
        expectedStatus: HttpStatus
    ) {
        productRepository.save(Product(ean = "000000000000000"))
        profileRepository.save(Profile(email = "this@exists.com", name = "this", surname = "exists"))
        val uri = URI(baseUrl)

        val ticketPost = TicketPostDTO(profileId, ean)

        val headers = HttpHeaders()
        headers.set("X-COM-PERSIST", "true")

        val request = HttpEntity(ticketPost, headers)

        val result = restTemplate.postForEntity(uri, request, String::class.java)

        //Verify request succeed
        Assertions.assertEquals(expectedStatus, result.statusCode)
    }

    fun updateTicketStatusOrPriorityLevelOrExpertIdTest(
        ticketId: Int = 1,
        operationUrl: String,
        body: Map<String, Any?>,
        expectedStatus: HttpStatus
    ) {
        val product = Product(ean = "000000000000000")
        val profile = Profile(email = "this@exists.com", name = "this", surname = "exists")
        productRepository.save(product)
        profileRepository.save(profile)
        expertRepository.save(Expert(email = "expert@email.com", name = "super", surname = "expert"))
        ticketRepository.save(Ticket(
            creationDate = Date(),
            expert = null,
            priorityLevel = null,
            product = product,
            profile = profile,
            status = "open"
        ))

        val baseUrl = "http://localhost:$port/API/tickets/$ticketId/$operationUrl"
        val uri = URI(baseUrl)

        val headers = HttpHeaders()
        headers.set("X-COM-PERSIST", "true")

        val request = HttpEntity(body, headers)

        val result = restTemplate.exchange(uri, HttpMethod.PUT, request, String::class.java)

        //Verify request succeed
        Assertions.assertEquals(expectedStatus, result.statusCode)
    }

    @Test
    fun creationTicketSuccessTest() {
        creationTicketTest(expectedStatus = HttpStatus.CREATED)
    }

    @Test
    fun creationTicketInvalidEanTest() {
        creationTicketTest(ean = "", expectedStatus = HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @Test
    fun creationTicketBlankProfileIdTest() {
        creationTicketTest(profileId = "", expectedStatus = HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @Test
    fun creationTicketNonExistingProfileIdTest() {
        creationTicketTest(profileId = "doesnt@exist.com", expectedStatus = HttpStatus.NOT_FOUND)
    }

    @Test
    fun creationTicketNonExistingEanTest() {
        creationTicketTest(ean = "000000000000001", expectedStatus = HttpStatus.NOT_FOUND)
    }

    @Test
    fun updateTicketStatusSuccessTest() {
        updateTicketStatusOrPriorityLevelOrExpertIdTest(
            operationUrl = "changeStatus",
            body = mapOf("status" to "in_progress"),
            expectedStatus = HttpStatus.OK
        )
    }

    @Test
    fun updateTicketStatusNullStatusTest() {
        updateTicketStatusOrPriorityLevelOrExpertIdTest(
            operationUrl = "changeStatus",
            body = mapOf("status" to null),
            expectedStatus = HttpStatus.UNPROCESSABLE_ENTITY
        )
    }

    @Test
    fun updateTicketStatusNonStringStatusTest() {
        updateTicketStatusOrPriorityLevelOrExpertIdTest(
            operationUrl = "changeStatus",
            body = mapOf("status" to 0),
            expectedStatus = HttpStatus.UNPROCESSABLE_ENTITY
        )
    }

    @Test
    fun updateTicketStatusNonExistingStatusTest() {
        updateTicketStatusOrPriorityLevelOrExpertIdTest(
            operationUrl = "changeStatus",
            body = mapOf("status" to "pending"),
            expectedStatus = HttpStatus.UNPROCESSABLE_ENTITY
        )
    }

    @Test
    fun updateTicketStatusNonExistingTicketTest() {
        updateTicketStatusOrPriorityLevelOrExpertIdTest(
            ticketId = 1000,
            operationUrl = "changeStatus",
            body = mapOf("status" to "in_progress"),
            expectedStatus = HttpStatus.NOT_FOUND
        )
    }

    @Test
    fun updateTicketStatusNotAllowedStateChangeTicketTest() {
        updateTicketStatusOrPriorityLevelOrExpertIdTest(
            operationUrl = "changeStatus",
            body = mapOf("status" to "reopened"),
            expectedStatus = HttpStatus.CONFLICT
        )
    }

    @Test
    fun updateTicketPrioritySuccessTest() {
        updateTicketStatusOrPriorityLevelOrExpertIdTest(
            operationUrl = "changePriority",
            body = mapOf("priorityLevel" to 0),
            expectedStatus = HttpStatus.OK
        )
    }

    @Test
    fun updateTicketPriorityNullPriorityTest() {
        updateTicketStatusOrPriorityLevelOrExpertIdTest(
            operationUrl = "changePriority",
            body = mapOf("priorityLevel" to null),
            expectedStatus = HttpStatus.UNPROCESSABLE_ENTITY
        )
    }

    @Test
    fun updateTicketPriorityNonIntStatusTest() {
        updateTicketStatusOrPriorityLevelOrExpertIdTest(
            operationUrl = "changePriority",
            body = mapOf("priorityLevel" to ""),
            expectedStatus = HttpStatus.UNPROCESSABLE_ENTITY
        )
    }

    @Test
    fun updateTicketPriorityNonExistingPriorityLevelTest() {
        updateTicketStatusOrPriorityLevelOrExpertIdTest(
            operationUrl = "changePriority",
            body = mapOf("priorityLevel" to 5),
            expectedStatus = HttpStatus.UNPROCESSABLE_ENTITY
        )
    }

    @Test
    fun updateTicketPriorityNonExistingTicketTest() {
        updateTicketStatusOrPriorityLevelOrExpertIdTest(
            ticketId = 1000,
            operationUrl = "changePriority",
            body = mapOf("priorityLevel" to 0),
            expectedStatus = HttpStatus.NOT_FOUND
        )
    }

    @Test
    fun updateTicketExpertSuccessTest() {
        updateTicketStatusOrPriorityLevelOrExpertIdTest(
            operationUrl = "changeExpert",
            body = mapOf("expertId" to 1),
            expectedStatus = HttpStatus.OK
        )
    }

    @Test
    fun updateTicketExpertNullExpertTest() {
        updateTicketStatusOrPriorityLevelOrExpertIdTest(
            operationUrl = "changeExpert",
            body = mapOf("expertId" to null),
            expectedStatus = HttpStatus.UNPROCESSABLE_ENTITY
        )
    }

    @Test
    fun updateTicketExpertNonIntExpertIdTest() {
        updateTicketStatusOrPriorityLevelOrExpertIdTest(
            operationUrl = "changeExpert",
            body = mapOf("expertId" to ""),
            expectedStatus = HttpStatus.UNPROCESSABLE_ENTITY
        )
    }

    @Test
    fun updateTicketExpertNonExistingTicketTest() {
        updateTicketStatusOrPriorityLevelOrExpertIdTest(
            ticketId = 1000,
            operationUrl = "changeExpert",
            body = mapOf("expertId" to 1),
            expectedStatus = HttpStatus.NOT_FOUND
        )
    }

    @Test
    fun updateTicketExpertNonExistingExpertIdTest() {
        updateTicketStatusOrPriorityLevelOrExpertIdTest(
            ticketId = 1,
            operationUrl = "changeExpert",
            body = mapOf("expertId" to 0),
            expectedStatus = HttpStatus.NOT_FOUND
        )
    }
}
