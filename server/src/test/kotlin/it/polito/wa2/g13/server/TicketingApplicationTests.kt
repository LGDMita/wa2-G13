package it.polito.wa2.g13.server

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import it.polito.wa2.g13.server.products.Product
import it.polito.wa2.g13.server.products.ProductRepository
import it.polito.wa2.g13.server.profiles.Profile
import it.polito.wa2.g13.server.profiles.ProfileRepository
import it.polito.wa2.g13.server.ticketing.experts.Expert
import it.polito.wa2.g13.server.ticketing.experts.ExpertDTO
import it.polito.wa2.g13.server.ticketing.experts.ExpertRepository
import it.polito.wa2.g13.server.ticketing.experts.toDTO
import it.polito.wa2.g13.server.ticketing.sectors.Sector
import it.polito.wa2.g13.server.ticketing.sectors.SectorDTO
import it.polito.wa2.g13.server.ticketing.sectors.SectorRepository
import it.polito.wa2.g13.server.ticketing.sectors.toDTO
import it.polito.wa2.g13.server.ticketing.tickets.*
import org.json.JSONObject
import org.junit.FixMethodOrder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.data.repository.findByIdOrNull
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
class TicketingApplicationTests {
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
    lateinit var sectorRepository: SectorRepository

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun t1TestGetAllTickets() {
        val baseUrl = "http://localhost:$port/API/tickets/"
        val uri = URI(baseUrl)
        val myProfile = Profile("luigimolinengo@gmail.com", "Luigi", "Molinengo")
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
        val myTicket2 = Ticket(
            profile = myProfile, product = myProduct, priorityLevel = 2, expert = myExpert,
            status = "closed", creationDate = Date(), messages = mutableSetOf()
        )

        profileRepository.save(myProfile)
        productRepository.save(myProduct)
        expertRepository.save(myExpert)
        ticketRepository.save(myTicket)
        ticketRepository.save(myTicket2)

        val result: ResponseEntity<String> = restTemplate.getForEntity(uri, String::class.java)

        val gson = Gson()
        val arrayTicketType = object : TypeToken<List<Ticket>>() {}.type
        val tickets: List<TicketDTO> = gson.fromJson(result.body, arrayTicketType)

        Assertions.assertEquals(HttpStatus.OK, result.statusCode)
        Assertions.assertEquals(2, tickets.size)

        ticketRepository.delete(myTicket)
        ticketRepository.delete(myTicket2)
        profileRepository.delete(myProfile)
        productRepository.delete(myProduct)
        expertRepository.delete(myExpert)
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun t2TestGetTicketById() {
        val baseUrl = "http://localhost:$port/API/tickets/2"
        val uri = URI(baseUrl)
        val myProfile = Profile("luigimolinengo@gmail.com", "Luigi", "Molinengo")
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
        val myTicket2 = Ticket(
            profile = myProfile, product = myProduct, priorityLevel = 2, expert = myExpert,
            status = "closed", creationDate = Date(), messages = mutableSetOf()
        )

        profileRepository.save(myProfile)
        productRepository.save(myProduct)
        expertRepository.save(myExpert)
        ticketRepository.save(myTicket)
        ticketRepository.save(myTicket2)

        val result: ResponseEntity<String> = restTemplate.getForEntity(uri, String::class.java)

        val gson = Gson()
        val ticketType = object : TypeToken<Ticket>() {}.type
        val ticket: Ticket = gson.fromJson(result.body, ticketType)

        Assertions.assertEquals(HttpStatus.OK, result.statusCode)
        Assertions.assertEquals(2, ticket.ticketId)

        ticketRepository.delete(myTicket)
        ticketRepository.delete(myTicket2)
        profileRepository.delete(myProfile)
        productRepository.delete(myProduct)
        expertRepository.delete(myExpert)
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun t3TestGetTicketByWrongId() {
        val baseUrl = "http://localhost:$port/API/tickets/5"
        val uri = URI(baseUrl)
        val myProfile = Profile("luigimolinengo@gmail.com", "Luigi", "Molinengo")
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
        val myTicket2 = Ticket(
            profile = myProfile, product = myProduct, priorityLevel = 2, expert = myExpert,
            status = "closed", creationDate = Date(), messages = mutableSetOf()
        )

        profileRepository.save(myProfile)
        productRepository.save(myProduct)
        expertRepository.save(myExpert)
        ticketRepository.save(myTicket)
        ticketRepository.save(myTicket2)

        val result: ResponseEntity<String> = restTemplate.getForEntity(uri, String::class.java)

        Assertions.assertEquals(HttpStatus.NOT_FOUND, result.statusCode)

        ticketRepository.delete(myTicket)
        ticketRepository.delete(myTicket2)
        profileRepository.delete(myProfile)
        productRepository.delete(myProduct)
        expertRepository.delete(myExpert)
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun t4TestGetFilteredTickets() {
        val baseUrl =
            "http://localhost:$port/API/tickets/?ean=4935531465706&profileId=luigimolinengo@gmail.com&priorityLevel=2"
        val uri = URI(baseUrl)
        val myProfile = Profile("luigimolinengo@gmail.com", "Luigi", "Molinengo")
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
        val myTicket2 = Ticket(
            profile = myProfile, product = myProduct, priorityLevel = 2, expert = myExpert,
            status = "closed", creationDate = Date(), messages = mutableSetOf()
        )

        profileRepository.save(myProfile)
        productRepository.save(myProduct)
        expertRepository.save(myExpert)
        ticketRepository.save(myTicket)
        ticketRepository.save(myTicket2)

        val result: ResponseEntity<String> = restTemplate.getForEntity(uri, String::class.java)

        val gson = Gson()
        val arrayTicketType = object : TypeToken<List<Ticket>>() {}.type
        val tickets: List<Ticket> = gson.fromJson(result.body, arrayTicketType)

        Assertions.assertEquals(HttpStatus.OK, result.statusCode)
        Assertions.assertEquals(1, tickets.size)

        ticketRepository.delete(myTicket)
        ticketRepository.delete(myTicket2)
        profileRepository.delete(myProfile)
        productRepository.delete(myProduct)
        expertRepository.delete(myExpert)
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun t5TestGetFilteredTicketsWrongParameters() {
        val baseUrl =
            "http://localhost:$port/API/tickets/?ean=4935531465706&profileId=luigimolinengo@gmail.com&priorityLevel=5"
        val uri = URI(baseUrl)
        val myProfile = Profile("luigimolinengo@gmail.com", "Luigi", "Molinengo")
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
        val myTicket2 = Ticket(
            profile = myProfile, product = myProduct, priorityLevel = 2, expert = myExpert,
            status = "closed", creationDate = Date(), messages = mutableSetOf()
        )

        profileRepository.save(myProfile)
        productRepository.save(myProduct)
        expertRepository.save(myExpert)
        ticketRepository.save(myTicket)
        ticketRepository.save(myTicket2)

        val result: ResponseEntity<String> = restTemplate.getForEntity(uri, String::class.java)

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result.statusCode)

        ticketRepository.delete(myTicket)
        ticketRepository.delete(myTicket2)
        profileRepository.delete(myProfile)
        productRepository.delete(myProduct)
        expertRepository.delete(myExpert)
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun t6TestGetTicketsEmpty() {
        val baseUrl = "http://localhost:$port/API/tickets/"
        val uri = URI(baseUrl)

        val result: ResponseEntity<String> = restTemplate.getForEntity(uri, String::class.java)

        val gson = Gson()
        val ticketType = object : TypeToken<List<Ticket>>() {}.type
        val tickets: List<Ticket> = gson.fromJson(result.body, ticketType)

        Assertions.assertEquals(HttpStatus.OK, result.statusCode)
        Assertions.assertEquals(true, tickets.isEmpty())
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun t7TestEditTicket() {
        val baseUrl = "http://localhost:$port/API/ticket/"
        val uri = URI(baseUrl)

        val myProfile = Profile("luigimolinengo@gmail.com", "Luigi", "Molinengo")
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

        profileRepository.save(myProfile)
        productRepository.save(myProduct)
        expertRepository.save(myExpert)
        ticketRepository.save(myTicket)

        myTicket.status = "closed"
        val request = HttpEntity(myTicket.toDTO())

        val result = restTemplate.exchange(uri, HttpMethod.PUT, request, String::class.java)

        Assertions.assertEquals(HttpStatus.OK, result.statusCode)
        Assertions.assertEquals("true", result?.body)

        ticketRepository.delete(myTicket)
        profileRepository.delete(myProfile)
        productRepository.delete(myProduct)
        expertRepository.delete(myExpert)
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun t8TestEditTicketNoTickedIdPassed() {
        val baseUrl = "http://localhost:$port/API/ticket/"
        val uri = URI(baseUrl)
        val myProfile = Profile("luigimolinengo@gmail.com", "Luigi", "Molinengo")
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

        profileRepository.save(myProfile)
        productRepository.save(myProduct)
        expertRepository.save(myExpert)
        ticketRepository.save(myTicket)

        myTicket.status = "closed"
        myTicket.ticketId = 5
        val request = HttpEntity(myTicket.toDTO())

        val result = restTemplate.exchange(uri, HttpMethod.PUT, request, String::class.java)

        Assertions.assertEquals(HttpStatus.NOT_FOUND, result.statusCode)

        ticketRepository.deleteById(1)
        profileRepository.delete(myProfile)
        productRepository.delete(myProduct)
        expertRepository.delete(myExpert)
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun t9TestEditTicketWrongParameterStatus() {
        val baseUrl = "http://localhost:$port/API/ticket/"
        val uri = URI(baseUrl)

        val myProfile = Profile("luigimolinengo@gmail.com", "Luigi", "Molinengo")
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

        profileRepository.save(myProfile)
        productRepository.save(myProduct)
        expertRepository.save(myExpert)
        ticketRepository.save(myTicket)

        myTicket.status = "aa"
        val request = HttpEntity(myTicket.toDTO())

        val result = restTemplate.exchange(uri, HttpMethod.PUT, request, String::class.java)

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result.statusCode)

        ticketRepository.delete(myTicket)
        profileRepository.delete(myProfile)
        productRepository.delete(myProduct)
        expertRepository.delete(myExpert)
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun t10TestEditTicketWrongParameterPriorityLevel() {
        val baseUrl = "http://localhost:$port/API/ticket/"
        val uri = URI(baseUrl)

        val myProfile = Profile("luigimolinengo@gmail.com", "Luigi", "Molinengo")
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

        profileRepository.save(myProfile)
        productRepository.save(myProduct)
        expertRepository.save(myExpert)
        ticketRepository.save(myTicket)

        myTicket.priorityLevel = 10
        val request = HttpEntity(myTicket.toDTO())

        val result = restTemplate.exchange(uri, HttpMethod.PUT, request, String::class.java)

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result.statusCode)

        ticketRepository.delete(myTicket)
        profileRepository.delete(myProfile)
        productRepository.delete(myProduct)
        expertRepository.delete(myExpert)
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun t11TestGetFilteredTicketsWrongEmail() {
        val baseUrl =
            "http://localhost:$port/API/tickets/?ean=4935531465706&profileId=luigimolinengo&priorityLevel=2"
        val uri = URI(baseUrl)
        val myProfile = Profile("luigimolinengo@gmail.com", "Luigi", "Molinengo")
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
        val myTicket2 = Ticket(
            profile = myProfile, product = myProduct, priorityLevel = 2, expert = myExpert,
            status = "closed", creationDate = Date(), messages = mutableSetOf()
        )

        profileRepository.save(myProfile)
        productRepository.save(myProduct)
        expertRepository.save(myExpert)
        ticketRepository.save(myTicket)
        ticketRepository.save(myTicket2)

        val result: ResponseEntity<String> = restTemplate.getForEntity(uri, String::class.java)

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result.statusCode)

        ticketRepository.delete(myTicket)
        ticketRepository.delete(myTicket2)
        profileRepository.delete(myProfile)
        productRepository.delete(myProduct)
        expertRepository.delete(myExpert)
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun t11TestGetFilteredTicketsWrongStatus() {
        val baseUrl =
            "http://localhost:$port/API/tickets/?ean=4935531465706&profileId=luigimolinengo@gmail.com&priorityLevel=2&status=aaa"
        val uri = URI(baseUrl)
        val myProfile = Profile("luigimolinengo@gmail.com", "Luigi", "Molinengo")
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
        val myTicket2 = Ticket(
            profile = myProfile, product = myProduct, priorityLevel = 2, expert = myExpert,
            status = "closed", creationDate = Date(), messages = mutableSetOf()
        )

        profileRepository.save(myProfile)
        productRepository.save(myProduct)
        expertRepository.save(myExpert)
        ticketRepository.save(myTicket)
        ticketRepository.save(myTicket2)

        val result: ResponseEntity<String> = restTemplate.getForEntity(uri, String::class.java)

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result.statusCode)

        ticketRepository.delete(myTicket)
        ticketRepository.delete(myTicket2)
        profileRepository.delete(myProfile)
        productRepository.delete(myProduct)
        expertRepository.delete(myExpert)
    }

    // ----

    fun updateTicketStatusOrPriorityLevelOrExpertIdTest(
        ticketId: Int = 1,
        operationUrl: String,
        body: Map<String, Any?>,
        expectedStatus: HttpStatus,
        expectedErrorMessage: String = ""
    ) {
        val product = Product(ean = "000000000000000")
        val profile = Profile(email = "this@exists.com", name = "this", surname = "exists")
        productRepository.save(product)
        profileRepository.save(profile)
        expertRepository.save(Expert(email = "expert@email.com", name = "super", surname = "expert"))
        ticketRepository.save(
            Ticket(
                creationDate = Date(),
                expert = null,
                priorityLevel = null,
                product = product,
                profile = profile,
                status = "open"
            )
        )

        val baseUrl = "http://localhost:$port/API/tickets/$ticketId/$operationUrl"
        val uri = URI(baseUrl)

        val request = HttpEntity(body)

        val result = restTemplate.exchange(uri, HttpMethod.PUT, request, String::class.java)

        //Verify request succeed
        Assertions.assertEquals(expectedStatus, result.statusCode)
        if (expectedErrorMessage != "")
            Assertions.assertEquals(expectedErrorMessage, JSONObject(result.body).get("detail"))
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun creationTicketSuccessTest() {
        creationTicketTest(expectedStatus = HttpStatus.CREATED)
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun creationTicketInvalidEanTest() {
        creationTicketTest(
            ean = "",
            expectedStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            expectedErrorMessage = "createTicket.ticketPostDTO.ean: The inserted input is not valid!"
        )
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun creationTicketBlankProfileIdTest() {
        creationTicketTest(
            profileId = "",
            expectedStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            expectedErrorMessage = "createTicket.ticketPostDTO.profileId: The inserted input is not valid!"
        )
    }

    fun creationTicketTest(
        baseUrl: String = "http://localhost:$port/API/tickets",
        profileId: String = "this@exists.com",
        ean: String = "000000000000000",
        expectedStatus: HttpStatus,
        expectedErrorMessage: String = ""
    ) {
        productRepository.save(Product(ean = "000000000000000"))
        profileRepository.save(Profile(email = "this@exists.com", name = "this", surname = "exists"))
        val uri = URI(baseUrl)

        val ticketPost = TicketPostDTO(profileId, ean)

        val request = HttpEntity(ticketPost)

        val result = restTemplate.postForEntity(uri, request, String::class.java)

        //Verify request succeed
        Assertions.assertEquals(expectedStatus, result.statusCode)
        if (expectedErrorMessage != "")
            Assertions.assertEquals(expectedErrorMessage, JSONObject(result.body).get("detail"))
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun creationTicketNonExistingProfileIdTest() {
        creationTicketTest(
            profileId = "doesnt@exist.com",
            expectedStatus = HttpStatus.NOT_FOUND,
            expectedErrorMessage = "Profile Not Found!"
        )
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun creationTicketNonExistingEanTest() {
        creationTicketTest(
            ean = "000000000000001",
            expectedStatus = HttpStatus.NOT_FOUND,
            expectedErrorMessage = "Product Not Found!"
        )
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun updateTicketStatusSuccessTest() {
        updateTicketStatusOrPriorityLevelOrExpertIdTest(
            operationUrl = "changeStatus",
            body = mapOf("status" to "in_progress"),
            expectedStatus = HttpStatus.OK
        )
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun updateTicketStatusNullStatusTest() {
        updateTicketStatusOrPriorityLevelOrExpertIdTest(
            operationUrl = "changeStatus",
            body = mapOf("status" to null),
            expectedStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            expectedErrorMessage = "The inserted input is not valid!"
        )
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun updateTicketStatusNonStringStatusTest() {
        updateTicketStatusOrPriorityLevelOrExpertIdTest(
            operationUrl = "changeStatus",
            body = mapOf("status" to 0),
            expectedStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            expectedErrorMessage = "The inserted input is not valid!"
        )
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun updateTicketStatusNonExistingStatusTest() {
        updateTicketStatusOrPriorityLevelOrExpertIdTest(
            operationUrl = "changeStatus",
            body = mapOf("status" to "pending"),
            expectedStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            expectedErrorMessage = "The inserted input is not valid!"
        )
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun updateTicketStatusNonExistingTicketTest() {
        updateTicketStatusOrPriorityLevelOrExpertIdTest(
            ticketId = 1000,
            operationUrl = "changeStatus",
            body = mapOf("status" to "in_progress"),
            expectedStatus = HttpStatus.NOT_FOUND,
            expectedErrorMessage = "Ticket Not Found!"
        )
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun updateTicketStatusNotAllowedStateChangeTicketTest() {
        updateTicketStatusOrPriorityLevelOrExpertIdTest(
            operationUrl = "changeStatus",
            body = mapOf("status" to "reopened"),
            expectedStatus = HttpStatus.CONFLICT,
            expectedErrorMessage = "Ticket state change not allowed!"
        )
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun updateTicketPrioritySuccessTest() {
        updateTicketStatusOrPriorityLevelOrExpertIdTest(
            operationUrl = "changePriority",
            body = mapOf("priorityLevel" to 0),
            expectedStatus = HttpStatus.OK
        )
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun updateTicketPriorityNullPriorityTest() {
        updateTicketStatusOrPriorityLevelOrExpertIdTest(
            operationUrl = "changePriority",
            body = mapOf("priorityLevel" to null),
            expectedStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            expectedErrorMessage = "The inserted input is not valid!"
        )
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun updateTicketPriorityNonIntStatusTest() {
        updateTicketStatusOrPriorityLevelOrExpertIdTest(
            operationUrl = "changePriority",
            body = mapOf("priorityLevel" to ""),
            expectedStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            expectedErrorMessage = "The inserted input is not valid!"
        )
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun updateTicketPriorityNonExistingPriorityLevelTest() {
        updateTicketStatusOrPriorityLevelOrExpertIdTest(
            operationUrl = "changePriority",
            body = mapOf("priorityLevel" to 5),
            expectedStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            expectedErrorMessage = "The inserted input is not valid!"
        )
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun updateTicketPriorityNonExistingTicketTest() {
        updateTicketStatusOrPriorityLevelOrExpertIdTest(
            ticketId = 1000,
            operationUrl = "changePriority",
            body = mapOf("priorityLevel" to 0),
            expectedStatus = HttpStatus.NOT_FOUND,
            expectedErrorMessage = "Ticket Not Found!"
        )
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun updateTicketExpertSuccessTest() {
        updateTicketStatusOrPriorityLevelOrExpertIdTest(
            operationUrl = "changeExpert",
            body = mapOf("expertId" to 1),
            expectedStatus = HttpStatus.OK
        )
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun updateTicketExpertNullExpertTest() {
        updateTicketStatusOrPriorityLevelOrExpertIdTest(
            operationUrl = "changeExpert",
            body = mapOf("expertId" to null),
            expectedStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            expectedErrorMessage = "The inserted input is not valid!"
        )
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun updateTicketExpertNonIntExpertIdTest() {
        updateTicketStatusOrPriorityLevelOrExpertIdTest(
            operationUrl = "changeExpert",
            body = mapOf("expertId" to ""),
            expectedStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            expectedErrorMessage = "The inserted input is not valid!"
        )
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun updateTicketExpertNonExistingTicketTest() {
        updateTicketStatusOrPriorityLevelOrExpertIdTest(
            ticketId = 1000,
            operationUrl = "changeExpert",
            body = mapOf("expertId" to 1),
            expectedStatus = HttpStatus.NOT_FOUND,
            expectedErrorMessage = "Ticket Not Found!"
        )
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun updateTicketExpertNonExistingExpertIdTest() {
        updateTicketStatusOrPriorityLevelOrExpertIdTest(
            ticketId = 1,
            operationUrl = "changeExpert",
            body = mapOf("expertId" to 0),
            expectedStatus = HttpStatus.NOT_FOUND,
            expectedErrorMessage = "Expert Not Found!"
        )
    }

    // ----

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun expertSetSuccessTest() {
        val baseUrl = "http://localhost:$port/API/experts"
        val uri = URI(baseUrl)
        val expertDTO = ExpertDTO(expertId = 1, name = "Will", surname = "Hunting", email = "will@gmail.com")

        val headers = HttpHeaders()
        headers.set("X-COM-PERSIST", "true")

        val request = HttpEntity(expertDTO, headers)

        val result = restTemplate.postForEntity(uri, request, String::class.java)

        Assertions.assertEquals(HttpStatus.CREATED, result.statusCode)
        Assertions.assertEquals(expertDTO, expertRepository.findByIdOrNull(1)?.toDTO())
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun expertSetConflictTest() {
        expertRepository.save(Expert(name = "William", surname = "Hunt", email = "will@gmail.com"))
        val baseUrl = "http://localhost:$port/API/experts"
        val uri = URI(baseUrl)
        val expertDTO = ExpertDTO(expertId = 1, name = "Will", surname = "Hunting", email = "will@gmail.com")

        val headers = HttpHeaders()
        headers.set("X-COM-PERSIST", "true")

        val request = HttpEntity(expertDTO, headers)

        val result = restTemplate.postForEntity(uri, request, String::class.java)

        Assertions.assertEquals(HttpStatus.CONFLICT, result.statusCode)
        Assertions.assertEquals(
            "\"detail\":\"An expert with this email address already exists!\"",
            result.body.toString().split(",")[3]
        )
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun expertSetUnprocessableEntityTest() {
        val baseUrl = "http://localhost:$port/API/experts"
        val uri = URI(baseUrl)
        val expertDTO = ExpertDTO(expertId = 1, name = "Will", surname = "", email = "will@ gmail.com")

        val headers = HttpHeaders()
        headers.set("X-COM-PERSIST", "true")

        val request = HttpEntity(expertDTO, headers)

        val result = restTemplate.postForEntity(uri, request, String::class.java)

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result.statusCode)
        Assertions.assertEquals(
            "\"detail\":\"The inserted input for the expert is not valid!\"",
            result.body.toString().split(",")[3]
        )
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun expertGetSuccess() {
        expertRepository.save(Expert(name = "William", surname = "Hunt", email = "will@gmail.com"))
        val baseUrl = "http://localhost:$port/API/experts/1"
        val uri = URI(baseUrl)

        val result = restTemplate.getForEntity(uri, String::class.java)

        Assertions.assertEquals(HttpStatus.OK, result.statusCode)
        Assertions.assertEquals(
            "{\"expertId\":1,\"name\":\"William\",\"surname\":\"Hunt\",\"email\":\"will@gmail.com\"}",
            result.body
        )
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun expertGetNotFound() {
        val baseUrl = "http://localhost:$port/API/experts/1"
        val uri = URI(baseUrl)

        val result = restTemplate.getForEntity(uri, String::class.java)

        Assertions.assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        Assertions.assertEquals(
            "\"detail\":\"Expert Not Found!\"",
            result.body.toString().split(",")[3]
        )
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun expertModifySuccessTest1() {
        expertRepository.save(Expert(name = "William", surname = "Hunt", email = "will@gmail.com"))
        val baseUrl = "http://localhost:$port/API/experts/1"
        val uri = URI(baseUrl)
        val expertDTO = ExpertDTO(expertId = 1, name = "Will", surname = "Hunting", email = "will@gmail.com")

        val headers = HttpHeaders()
        headers.set("X-COM-PERSIST", "true")

        val request = HttpEntity(expertDTO, headers)

        val result = restTemplate.exchange(uri, HttpMethod.PUT, request, String::class.java)

        Assertions.assertEquals(HttpStatus.OK, result.statusCode)
        val expert = expertRepository.findByIdOrNull(1)
        Assertions.assertEquals("Will", expert?.name)
        Assertions.assertEquals("Hunting", expert?.surname)
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun expertModifySuccessTest2() {
        expertRepository.save(Expert(name = "William", surname = "Hunt", email = "will@gmail.com"))
        val baseUrl = "http://localhost:$port/API/experts/1"
        val uri = URI(baseUrl)
        val expertDTO = ExpertDTO(expertId = 1, name = "William", surname = "Hunt", email = "william@gmail.com")

        val headers = HttpHeaders()
        headers.set("X-COM-PERSIST", "true")

        val request = HttpEntity(expertDTO, headers)

        val result = restTemplate.exchange(uri, HttpMethod.PUT, request, String::class.java)

        Assertions.assertEquals(HttpStatus.OK, result.statusCode)
        Assertions.assertEquals("william@gmail.com", expertRepository.findByIdOrNull(1)?.email)
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun expertModifyUnprocessableEntityTest() {
        expertRepository.save(Expert(name = "William", surname = "Hunt", email = "will@gmail.com"))
        val baseUrl = "http://localhost:$port/API/experts/1"
        val uri = URI(baseUrl)
        val expertDTO = ExpertDTO(expertId = 1, name = "William", surname = "Hunt", email = "william @gmail.com")

        val headers = HttpHeaders()
        headers.set("X-COM-PERSIST", "true")

        val request = HttpEntity(expertDTO, headers)

        val result = restTemplate.exchange(uri, HttpMethod.PUT, request, String::class.java)

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result.statusCode)
        Assertions.assertEquals(
            "\"detail\":\"The inserted input for the expert is not valid!\"",
            result.body.toString().split(",")[3]
        )
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun expertModifyNotFoundTest() {
        expertRepository.save(Expert(name = "William", surname = "Hunt", email = "will@gmail.com"))
        val baseUrl = "http://localhost:$port/API/experts/2"
        val uri = URI(baseUrl)
        val expertDTO = ExpertDTO(expertId = 2, name = "Will", surname = "Hunt", email = "william@gmail.com")

        val headers = HttpHeaders()
        headers.set("X-COM-PERSIST", "true")

        val request = HttpEntity(expertDTO, headers)

        val result = restTemplate.exchange(uri, HttpMethod.PUT, request, String::class.java)

        Assertions.assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        Assertions.assertEquals(
            "\"detail\":\"Expert Not Found!\"",
            result.body.toString().split(",")[3]
        )
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun expertModifyConflictTest() {
        expertRepository.save(Expert(name = "William", surname = "Hunt", email = "will@gmail.com"))
        expertRepository.save(Expert(name = "Will", surname = "Hunting", email = "william@gmail.com"))
        val baseUrl = "http://localhost:$port/API/experts/2"
        val uri = URI(baseUrl)
        val expertDTO = ExpertDTO(expertId = 2, name = "Will", surname = "Hunt", email = "will@gmail.com")

        val headers = HttpHeaders()
        headers.set("X-COM-PERSIST", "true")

        val request = HttpEntity(expertDTO, headers)

        val result = restTemplate.exchange(uri, HttpMethod.PUT, request, String::class.java)

        Assertions.assertEquals(HttpStatus.CONFLICT, result.statusCode)
        Assertions.assertEquals(
            "\"detail\":\"An expert with this email address already exists!\"",
            result.body.toString().split(",")[3]
        )
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun expertDeleteSuccessTest() {
        expertRepository.save(Expert(name = "William", surname = "Hunt", email = "will@gmail.com"))
        val baseUrl = "http://localhost:$port/API/experts/1"
        val uri = URI(baseUrl)

        val headers = HttpHeaders()
        headers.set("X-COM-PERSIST", "true")

        val request = HttpEntity(null, headers)

        val result = restTemplate.exchange(uri, HttpMethod.DELETE, request, String::class.java)

        Assertions.assertEquals(HttpStatus.OK, result.statusCode)
        Assertions.assertEquals(null, expertRepository.findByIdOrNull(1))

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun expertDeleteNotFoundTest() {
        val baseUrl = "http://localhost:$port/API/experts/1"
        val uri = URI(baseUrl)

        val headers = HttpHeaders()
        headers.set("X-COM-PERSIST", "true")

        val request = HttpEntity(null, headers)

        val result = restTemplate.exchange(uri, HttpMethod.DELETE, request, String::class.java)

        Assertions.assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        Assertions.assertEquals(
            "\"detail\":\"Expert Not Found!\"",
            result.body.toString().split(",")[3]
        )
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun expertsBySectorGetSuccessTest() {
        val expert1 = Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
        val expert2 = Expert(name = "Will", surname = "Hunting", email = "will@gmail.com")
        expertRepository.save(expert1)
        expertRepository.save(expert2)
        val sector = Sector(sectorId = 1, name = "linux")
        expert1.addSector(sector)
        expert2.addSector(sector)
        sector.addExpert(expert1)
        sector.addExpert(expert2)
        sectorRepository.save(sector)
        expertRepository.save(expert1)
        expertRepository.save(expert2)

        val baseUrl = "http://localhost:$port/API/experts/?sectorName=LINUX"
        val uri = URI(baseUrl)

        val result = restTemplate.getForEntity(uri, String::class.java)

        Assertions.assertEquals(HttpStatus.OK, result.statusCode)
        Assertions.assertEquals(
            "[{\"expertId\":1,\"name\":\"William\",\"surname\":\"Hunt\",\"email\":\"william@gmail.com\"},{\"expertId\":2,\"name\":\"Will\",\"surname\":\"Hunting\",\"email\":\"will@gmail.com\"}]",
            result.body
        )
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun expertsBySectorGetSectorsNotFoundTest() {
        val expert1 = Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
        val expert2 = Expert(name = "Will", surname = "Hunting", email = "will@gmail.com")
        expertRepository.save(expert1)
        expertRepository.save(expert2)

        val baseUrl = "http://localhost:$port/API/experts/?sectorName=LINUX"
        val uri = URI(baseUrl)

        val result = restTemplate.getForEntity(uri, String::class.java)

        Assertions.assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        Assertions.assertEquals(
            "\"detail\":\"There are currently no sectors in the DB!\"",
            result.body.toString().split(",")[3]
        )
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun expertsBySectorGetSectorNotFoundTest() {
        val expert1 = Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
        val expert2 = Expert(name = "Will", surname = "Hunting", email = "will@gmail.com")
        expertRepository.save(expert1)
        expertRepository.save(expert2)
        val sector = Sector(sectorId = 1, name = "Hardware")
        expert1.addSector(sector)
        sector.addExpert(expert1)
        sectorRepository.save(sector)
        expertRepository.save(expert1)

        val baseUrl = "http://localhost:$port/API/experts/?sectorName=LINUX"
        val uri = URI(baseUrl)

        val result = restTemplate.getForEntity(uri, String::class.java)

        Assertions.assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        Assertions.assertEquals(
            "\"detail\":\"The selected sector does not exist!\"",
            result.body.toString().split(",")[3]
        )
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun expertsBySectorGetExpertOfSelectedSectorNotFoundTest() {
        val expert1 = Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
        val expert2 = Expert(name = "Will", surname = "Hunting", email = "will@gmail.com")
        expertRepository.save(expert1)
        expertRepository.save(expert2)
        val sector = Sector(sectorId = 1, name = "linux")
        sectorRepository.save(sector)

        val baseUrl = "http://localhost:$port/API/experts/?sectorName=LINUX"
        val uri = URI(baseUrl)

        val result = restTemplate.getForEntity(uri, String::class.java)

        Assertions.assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        Assertions.assertEquals(
            "\"detail\":\"There are no experts associated with the selected sector!\"",
            result.body.toString().split(",")[3]
        )
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun sectorsAllGetSuccessTest() {
        val expert1 = Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
        val expert2 = Expert(name = "Will", surname = "Hunting", email = "will@gmail.com")
        expertRepository.save(expert1)
        expertRepository.save(expert2)
        val sector1 = Sector(sectorId = 1, name = "linux")
        val sector2 = Sector(sectorId = 2, name = "wi-fi")
        expert1.addSector(sector1)
        expert1.addSector(sector2)
        sector1.addExpert(expert1)
        sector2.addExpert(expert1)
        sectorRepository.save(sector1)
        sectorRepository.save(sector2)
        expertRepository.save(expert1)

        val baseUrl = "http://localhost:$port/API/experts/sectors"
        val uri = URI(baseUrl)

        val result = restTemplate.getForEntity(uri, String::class.java)

        Assertions.assertEquals(HttpStatus.OK, result.statusCode)
        Assertions.assertEquals(
            "[{\"sectorId\":1,\"name\":\"linux\"},{\"sectorId\":2,\"name\":\"wi-fi\"}]",
            result.body
        )
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun sectorsAllGetNotFoundTest() {
        val expert1 = Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
        val expert2 = Expert(name = "Will", surname = "Hunting", email = "will@gmail.com")
        expertRepository.save(expert1)
        expertRepository.save(expert2)

        val baseUrl = "http://localhost:$port/API/experts/sectors"
        val uri = URI(baseUrl)

        val result = restTemplate.getForEntity(uri, String::class.java)

        Assertions.assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        Assertions.assertEquals(
            "\"detail\":\"There are currently no sectors in the DB!\"",
            result.body.toString().split(",")[3]
        )
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun sectorsOfExpertGetSuccessTest() {
        val expert1 = Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
        val expert2 = Expert(name = "Will", surname = "Hunting", email = "will@gmail.com")
        expertRepository.save(expert1)
        expertRepository.save(expert2)
        val sector1 = Sector(sectorId = 1, name = "linux")
        val sector2 = Sector(sectorId = 2, name = "wi-fi")
        expert1.addSector(sector1)
        expert1.addSector(sector2)
        sector1.addExpert(expert1)
        sector2.addExpert(expert1)
        sectorRepository.save(sector1)
        sectorRepository.save(sector2)
        expertRepository.save(expert1)

        val baseUrl = "http://localhost:$port/API/experts/1/sectors"
        val uri = URI(baseUrl)

        val result = restTemplate.getForEntity(uri, String::class.java)

        Assertions.assertEquals(HttpStatus.OK, result.statusCode)
        Assertions.assertEquals(
            "[{\"sectorId\":1,\"name\":\"linux\"},{\"sectorId\":2,\"name\":\"wi-fi\"}]",
            result.body
        )
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun sectorsOfExpertGetExpertNotFoundTest() {
        val expert1 = Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
        val expert2 = Expert(name = "Will", surname = "Hunting", email = "will@gmail.com")
        expertRepository.save(expert1)
        expertRepository.save(expert2)
        val sector1 = Sector(sectorId = 1, name = "linux")
        val sector2 = Sector(sectorId = 2, name = "wi-fi")
        expert1.addSector(sector1)
        expert1.addSector(sector2)
        sector1.addExpert(expert1)
        sector2.addExpert(expert1)
        sectorRepository.save(sector1)
        sectorRepository.save(sector2)
        expertRepository.save(expert1)

        val baseUrl = "http://localhost:$port/API/experts/3/sectors"
        val uri = URI(baseUrl)

        val result = restTemplate.getForEntity(uri, String::class.java)

        Assertions.assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        Assertions.assertEquals(
            "\"detail\":\"Expert Not Found!\"",
            result.body.toString().split(",")[3]
        )
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun sectorsOfExpertGetExpertSectorsNotFoundTest() {
        val expert1 = Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
        val expert2 = Expert(name = "Will", surname = "Hunting", email = "will@gmail.com")
        expertRepository.save(expert1)
        expertRepository.save(expert2)
        val sector1 = Sector(sectorId = 1, name = "linux")
        val sector2 = Sector(sectorId = 2, name = "wi-fi")
        expert1.addSector(sector1)
        expert1.addSector(sector2)
        sector1.addExpert(expert1)
        sector2.addExpert(expert1)
        sectorRepository.save(sector1)
        sectorRepository.save(sector2)
        expertRepository.save(expert1)

        val baseUrl = "http://localhost:$port/API/experts/2/sectors"
        val uri = URI(baseUrl)

        val result = restTemplate.getForEntity(uri, String::class.java)

        Assertions.assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        Assertions.assertEquals(
            "\"detail\":\"There are no sectors associated with the selected expert!\"",
            result.body.toString().split(",")[3]
        )
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun sectorForExpertSetSuccessTest() {
        val expert1 = Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
        expertRepository.save(expert1)
        val sectorDTO = SectorDTO(sectorId = 1, name = "linux")
        val baseUrl = "http://localhost:$port/API/experts/1/sectors"
        val uri = URI(baseUrl)

        val headers = HttpHeaders()
        headers.set("X-COM-PERSIST", "true")

        val request = HttpEntity(sectorDTO, headers)

        val result = restTemplate.postForEntity(uri, request, String::class.java)

        Assertions.assertEquals(HttpStatus.OK, result.statusCode)
        Assertions.assertEquals(sectorDTO, sectorRepository.findByIdOrNull(1)?.toDTO())

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun sectorForExpertSetUnprocessableEntityTest() {
        val expert1 = Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
        expertRepository.save(expert1)
        val sectorDTO = SectorDTO(sectorId = 1, name = "")
        val baseUrl = "http://localhost:$port/API/experts/1/sectors"
        val uri = URI(baseUrl)

        val headers = HttpHeaders()
        headers.set("X-COM-PERSIST", "true")

        val request = HttpEntity(sectorDTO, headers)

        val result = restTemplate.postForEntity(uri, request, String::class.java)

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result.statusCode)
        Assertions.assertEquals(
            "\"detail\":\"The inserted input for the sector is not valid!\"",
            result.body.toString().split(",")[3]
        )
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun sectorForExpertSetExpertNotFoundTest() {
        val expert1 = Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
        expertRepository.save(expert1)
        val sectorDTO = SectorDTO(sectorId = 1, name = "cellular network")
        val baseUrl = "http://localhost:$port/API/experts/2/sectors"
        val uri = URI(baseUrl)

        val headers = HttpHeaders()
        headers.set("X-COM-PERSIST", "true")

        val request = HttpEntity(sectorDTO, headers)

        val result = restTemplate.postForEntity(uri, request, String::class.java)

        Assertions.assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        Assertions.assertEquals(
            "\"detail\":\"Expert Not Found!\"",
            result.body.toString().split(",")[3]
        )
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun sectorForExpertDeleteSuccessTest() {
        val expert1 = Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
        val expert2 = Expert(name = "Will", surname = "Hunting", email = "will@gmail.com")
        expertRepository.save(expert1)
        expertRepository.save(expert2)
        val sector1 = Sector(sectorId = 1, name = "linux")
        val sector2 = Sector(sectorId = 2, name = "wi-fi")
        expert1.addSector(sector1)
        expert1.addSector(sector2)
        sector1.addExpert(expert1)
        sector2.addExpert(expert1)
        sectorRepository.save(sector1)
        sectorRepository.save(sector2)
        expertRepository.save(expert1)
        val baseUrl = "http://localhost:$port/API/experts/1/sectors/1"
        val uri = URI(baseUrl)

        val headers = HttpHeaders()
        headers.set("X-COM-PERSIST", "true")

        val request = HttpEntity(null, headers)

        val result = restTemplate.exchange(uri, HttpMethod.DELETE, request, String::class.java)

        Assertions.assertEquals(HttpStatus.OK, result.statusCode)
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun sectorForExpertDeleteExpertNotFoundTest() {
        val expert1 = Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
        val expert2 = Expert(name = "Will", surname = "Hunting", email = "will@gmail.com")
        expertRepository.save(expert1)
        expertRepository.save(expert2)
        val sector1 = Sector(sectorId = 1, name = "linux")
        val sector2 = Sector(sectorId = 2, name = "wi-fi")
        expert1.addSector(sector1)
        expert1.addSector(sector2)
        sector1.addExpert(expert1)
        sector2.addExpert(expert1)
        sectorRepository.save(sector1)
        sectorRepository.save(sector2)
        expertRepository.save(expert1)
        val baseUrl = "http://localhost:$port/API/experts/3/sectors/1"
        val uri = URI(baseUrl)

        val headers = HttpHeaders()
        headers.set("X-COM-PERSIST", "true")

        val request = HttpEntity(null, headers)

        val result = restTemplate.exchange(uri, HttpMethod.DELETE, request, String::class.java)

        Assertions.assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        Assertions.assertEquals(
            "\"detail\":\"Expert Not Found!\"",
            result.body.toString().split(",")[3]
        )
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun sectorForExpertDeleteSectorsNotFoundTest() {
        val expert1 = Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
        val expert2 = Expert(name = "Will", surname = "Hunting", email = "will@gmail.com")
        expertRepository.save(expert1)
        expertRepository.save(expert2)

        val baseUrl = "http://localhost:$port/API/experts/1/sectors/1"
        val uri = URI(baseUrl)

        val headers = HttpHeaders()
        headers.set("X-COM-PERSIST", "true")

        val request = HttpEntity(null, headers)

        val result = restTemplate.exchange(uri, HttpMethod.DELETE, request, String::class.java)

        Assertions.assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        Assertions.assertEquals(
            "\"detail\":\"There are currently no sectors in the DB!\"",
            result.body.toString().split(",")[3]
        )
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun sectorForExpertDeleteSectorNotFoundTest() {
        val expert1 = Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
        val expert2 = Expert(name = "Will", surname = "Hunting", email = "will@gmail.com")
        expertRepository.save(expert1)
        expertRepository.save(expert2)
        val sector1 = Sector(sectorId = 1, name = "linux")
        val sector2 = Sector(sectorId = 2, name = "wi-fi")
        expert1.addSector(sector1)
        sectorRepository.save(sector1)
        sectorRepository.save(sector2)
        expertRepository.save(expert1)

        val baseUrl = "http://localhost:$port/API/experts/1/sectors/3"
        val uri = URI(baseUrl)

        val headers = HttpHeaders()
        headers.set("X-COM-PERSIST", "true")

        val request = HttpEntity(null, headers)

        val result = restTemplate.exchange(uri, HttpMethod.DELETE, request, String::class.java)

        Assertions.assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        Assertions.assertEquals(
            "\"detail\":\"The selected sector does not exist!\"",
            result.body.toString().split(",")[3]
        )
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun sectorForExpertDeleteExpertSectorsNotFoundTest() {
        val expert1 = Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
        val expert2 = Expert(name = "Will", surname = "Hunting", email = "will@gmail.com")
        expertRepository.save(expert1)
        expertRepository.save(expert2)
        val sector1 = Sector(sectorId = 1, name = "linux")
        val sector2 = Sector(sectorId = 2, name = "wi-fi")
        expert1.addSector(sector1)
        sectorRepository.save(sector1)
        sectorRepository.save(sector2)
        expertRepository.save(expert1)

        val baseUrl = "http://localhost:$port/API/experts/2/sectors/1"
        val uri = URI(baseUrl)

        val headers = HttpHeaders()
        headers.set("X-COM-PERSIST", "true")

        val request = HttpEntity(null, headers)

        val result = restTemplate.exchange(uri, HttpMethod.DELETE, request, String::class.java)

        Assertions.assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        Assertions.assertEquals(
            "\"detail\":\"There are no sectors associated with the selected expert!\"",
            result.body.toString().split(",")[3]
        )
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun sectorForExpertDeleteExpertSectorNotFoundTest() {
        val expert1 = Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
        val expert2 = Expert(name = "Will", surname = "Hunting", email = "will@gmail.com")
        expertRepository.save(expert1)
        expertRepository.save(expert2)
        val sector1 = Sector(sectorId = 1, name = "linux")
        val sector2 = Sector(sectorId = 2, name = "wi-fi")
        expert1.addSector(sector1)
        sectorRepository.save(sector1)
        sectorRepository.save(sector2)
        expertRepository.save(expert1)

        val baseUrl = "http://localhost:$port/API/experts/1/sectors/2"
        val uri = URI(baseUrl)

        val headers = HttpHeaders()
        headers.set("X-COM-PERSIST", "true")

        val request = HttpEntity(null, headers)

        val result = restTemplate.exchange(uri, HttpMethod.DELETE, request, String::class.java)

        Assertions.assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        Assertions.assertEquals(
            "\"detail\":\"The selected sector is not linked with the selected expert!\"",
            result.body.toString().split(",")[3]
        )
    }

}