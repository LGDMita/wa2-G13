package it.polito.wa2.g13.server

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import it.polito.wa2.g13.server.jwtAuth.AuthService
import it.polito.wa2.g13.server.jwtAuth.LoginDTO
import it.polito.wa2.g13.server.products.Product
import it.polito.wa2.g13.server.products.ProductRepository
import it.polito.wa2.g13.server.profiles.Profile
import it.polito.wa2.g13.server.profiles.ProfileRepository
import it.polito.wa2.g13.server.purchase.Purchase
import it.polito.wa2.g13.server.purchase.PurchaseRepository
import it.polito.wa2.g13.server.ticketing.experts.Expert
import it.polito.wa2.g13.server.ticketing.experts.ExpertRepository
import it.polito.wa2.g13.server.ticketing.tickets.*
import it.polito.wa2.g13.server.warranty.Warranty
import it.polito.wa2.g13.server.warranty.WarrantyRepository
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
class TicketTests {
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
    lateinit var authService: AuthService

    @Autowired
    lateinit var warrantyRepository: WarrantyRepository

    @Autowired
    lateinit var purchaseRepository: PurchaseRepository

    fun quickProfile(): Profile {
        return Profile(
            id = "id",
            username = "luigimoli",
            email = "luigimolinengo@gmail.com",
            name = "Luigi",
            surname = "Molinengo"
        )
    }

    fun quickExpert(): Expert {
        return Expert(
            id = "id",
            username = "giomalnati",
            name = "Giovanni",
            surname = "Malnati",
            email = "giovanni.malnati@polito.it"
        )
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun t1TestGetAllTickets() {
        val baseUrl = "http://localhost:$port/API/tickets/"
        val uri = URI(baseUrl)
        val myProfile = quickProfile()
        val myProduct = Product(
            "4935531465706",
            "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
            "JMT"
        )
        val myExpert = quickExpert()
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

        val loginDTO= LoginDTO(username = "expert", password = "password")
        val token= authService.login(loginDTO)?.jwtAccessToken
        val headers = HttpHeaders()
        if (token != null) {
            headers.setBearerAuth(token)
        }
        headers.set("X-COM-PERSIST", "true")
        val request = HttpEntity(null, headers)
        val result = restTemplate.exchange(uri, HttpMethod.GET, request, String::class.java)

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
    fun t1BTestGetAllTicketsUnAuthenticated() {
        val baseUrl = "http://localhost:$port/API/tickets/"
        val uri = URI(baseUrl)
        val myProfile = quickProfile()
        val myProduct = Product(
            "4935531465706",
            "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
            "JMT"
        )
        val myExpert = quickExpert()
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

        val headers = HttpHeaders()
        headers.set("X-COM-PERSIST", "true")
        val request = HttpEntity(null, headers)
        val result = restTemplate.exchange(uri, HttpMethod.GET, request, String::class.java)

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, result.statusCode)

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
        val myProfile = quickProfile()
        val myProduct = Product(
            "4935531465706",
            "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
            "JMT"
        )
        val myExpert = quickExpert()
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

        val loginDTO= LoginDTO(username = "expert", password = "password")
        val token= authService.login(loginDTO)?.jwtAccessToken
        val headers = HttpHeaders()
        if (token != null) {
            headers.setBearerAuth(token)
        }
        headers.set("X-COM-PERSIST", "true")
        val request = HttpEntity(null, headers)
        val result = restTemplate.exchange(uri, HttpMethod.GET, request, String::class.java)

        val gson = Gson()
        val ticketType = object : TypeToken<TicketDTO>() {}.type
        val ticket: TicketDTO = gson.fromJson(result.body, ticketType)

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
        val myProfile = quickProfile()
        val myProduct = Product(
            "4935531465706",
            "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
            "JMT"
        )
        val myExpert = quickExpert()
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

        val loginDTO= LoginDTO(username = "expert", password = "password")
        val token= authService.login(loginDTO)?.jwtAccessToken
        val headers = HttpHeaders()
        if (token != null) {
            headers.setBearerAuth(token)
        }
        headers.set("X-COM-PERSIST", "true")
        val request = HttpEntity(null, headers)
        val result = restTemplate.exchange(uri, HttpMethod.GET, request, String::class.java)

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
        val myProfile = quickProfile()
        val myProduct = Product(
            "4935531465706",
            "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
            "JMT"
        )
        val myExpert = quickExpert()
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

        val loginDTO= LoginDTO(username = "expert", password = "password")
        val token= authService.login(loginDTO)?.jwtAccessToken
        val headers = HttpHeaders()
        if (token != null) {
            headers.setBearerAuth(token)
        }
        headers.set("X-COM-PERSIST", "true")
        val request = HttpEntity(null, headers)
        val result = restTemplate.exchange(uri, HttpMethod.GET, request, String::class.java)

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
        val myProfile = quickProfile()
        val myProduct = Product(
            "4935531465706",
            "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
            "JMT"
        )
        val myExpert = quickExpert()
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

        val loginDTO= LoginDTO(username = "expert", password = "password")
        val token= authService.login(loginDTO)?.jwtAccessToken
        val headers = HttpHeaders()
        if (token != null) {
            headers.setBearerAuth(token)
        }
        headers.set("X-COM-PERSIST", "true")
        val request = HttpEntity(null, headers)
        val result = restTemplate.exchange(uri, HttpMethod.GET, request, String::class.java)

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

        val loginDTO= LoginDTO(username = "expert", password = "password")
        val token= authService.login(loginDTO)?.jwtAccessToken
        val headers = HttpHeaders()
        if (token != null) {
            headers.setBearerAuth(token)
        }
        headers.set("X-COM-PERSIST", "true")
        val request = HttpEntity(null, headers)
        val result = restTemplate.exchange(uri, HttpMethod.GET, request, String::class.java)

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

        val myProfile = quickProfile()
        val myProduct = Product(
            "4935531465706",
            "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
            "JMT"
        )
        val myExpert = quickExpert()
        val myTicket = Ticket(
            profile = myProfile, product = myProduct, priorityLevel = 1, expert = myExpert,
            status = "open", creationDate = Date(), messages = mutableSetOf()
        )

        profileRepository.save(myProfile)
        productRepository.save(myProduct)
        expertRepository.save(myExpert)
        ticketRepository.save(myTicket)

        myTicket.status = "closed"
        val loginDTO= LoginDTO(username = "expert", password = "password")
        val token= authService.login(loginDTO)?.jwtAccessToken
        val headers = HttpHeaders()
        if (token != null) {
            headers.setBearerAuth(token)
        }
        headers.set("X-COM-PERSIST", "true")
        val request = HttpEntity(myTicket.toDTO(), headers)
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
        val myProfile = quickProfile()
        val myProduct = Product(
            "4935531465706",
            "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
            "JMT"
        )
        val myExpert = quickExpert()
        val myTicket = Ticket(
            profile = myProfile, product = myProduct, priorityLevel = 1, expert = myExpert,
            status = "open", creationDate = Date(), messages = mutableSetOf()
        )

        profileRepository.save(myProfile)
        productRepository.save(myProduct)
        expertRepository.save(myExpert)
        ticketRepository.save(myTicket)

        val t = myTicket.toDTO()
        t.ticketId = 5000

        val loginDTO= LoginDTO(username = "expert", password = "password")
        val token= authService.login(loginDTO)?.jwtAccessToken
        val headers = HttpHeaders()
        if (token != null) {
            headers.setBearerAuth(token)
        }
        headers.set("X-COM-PERSIST", "true")
        val request = HttpEntity(t, headers)
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

        val myProfile = quickProfile()
        val myProduct = Product(
            "4935531465706",
            "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
            "JMT"
        )
        val myExpert = quickExpert()
        val myTicket = Ticket(
            profile = myProfile, product = myProduct, priorityLevel = 1, expert = myExpert,
            status = "open", creationDate = Date(), messages = mutableSetOf()
        )

        profileRepository.save(myProfile)
        productRepository.save(myProduct)
        expertRepository.save(myExpert)
        ticketRepository.save(myTicket)

        myTicket.status = "aa"

        val loginDTO= LoginDTO(username = "expert", password = "password")
        val token= authService.login(loginDTO)?.jwtAccessToken
        val headers = HttpHeaders()
        if (token != null) {
            headers.setBearerAuth(token)
        }
        headers.set("X-COM-PERSIST", "true")
        val request = HttpEntity(myTicket.toDTO(), headers)
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

        val myProfile = quickProfile()
        val myProduct = Product(
            "4935531465706",
            "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
            "JMT"
        )
        val myExpert = quickExpert()
        val myTicket = Ticket(
            profile = myProfile, product = myProduct, priorityLevel = 1, expert = myExpert,
            status = "open", creationDate = Date(), messages = mutableSetOf()
        )

        profileRepository.save(myProfile)
        productRepository.save(myProduct)
        expertRepository.save(myExpert)
        ticketRepository.save(myTicket)

        myTicket.priorityLevel = 10

        val loginDTO= LoginDTO(username = "expert", password = "password")
        val token= authService.login(loginDTO)?.jwtAccessToken
        val headers = HttpHeaders()
        if (token != null) {
            headers.setBearerAuth(token)
        }
        headers.set("X-COM-PERSIST", "true")
        val request = HttpEntity(myTicket.toDTO(), headers)
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
        val myProfile = quickProfile()
        val myProduct = Product(
            "4935531465706",
            "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
            "JMT"
        )
        val myExpert = quickExpert()
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

        val loginDTO= LoginDTO(username = "expert", password = "password")
        val token= authService.login(loginDTO)?.jwtAccessToken
        val headers = HttpHeaders()
        if (token != null) {
            headers.setBearerAuth(token)
        }
        headers.set("X-COM-PERSIST", "true")
        val request = HttpEntity(null, headers)
        val result = restTemplate.exchange(uri, HttpMethod.GET, request, String::class.java)

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
        val myProfile = quickProfile()
        val myProduct = Product(
            "4935531465706",
            "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
            "JMT"
        )
        val myExpert = quickExpert()
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

        val loginDTO= LoginDTO(username = "expert", password = "password")
        val token= authService.login(loginDTO)?.jwtAccessToken
        val headers = HttpHeaders()
        if (token != null) {
            headers.setBearerAuth(token)
        }
        headers.set("X-COM-PERSIST", "true")
        val request = HttpEntity(null, headers)
        val result = restTemplate.exchange(uri, HttpMethod.GET, request, String::class.java)

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result.statusCode)

        ticketRepository.delete(myTicket)
        ticketRepository.delete(myTicket2)
        profileRepository.delete(myProfile)
        productRepository.delete(myProduct)
        expertRepository.delete(myExpert)
    }

    // ---

    fun updateTicketStatusOrPriorityLevelOrExpertIdTest(
        ticketId: Int = 1,
        operationUrl: String,
        body: Map<String, Any?>,
        expectedStatus: HttpStatus,
        expectedErrorMessage: String = ""
    ) {
        val product = Product(ean = "000000000000000")
        val profile = Profile(
            id = "id",
            username = "username",
            email = "this@exists.com",
            name = "this",
            surname = "exists"
        )
        productRepository.save(product)
        profileRepository.save(profile)
        expertRepository.save(Expert(
            id = "id",
            username = "username",
            email = "expert@email.com",
            name = "super",
            surname = "expert")
        )
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

        val loginDTO= LoginDTO(username = "expert", password = "password")
        val token= authService.login(loginDTO)?.jwtAccessToken
        val headers = HttpHeaders()
        if (token != null) {
            headers.setBearerAuth(token)
        }
        headers.set("X-COM-PERSIST", "true")
        val request = HttpEntity(body, headers)
        val result = restTemplate.exchange(uri, HttpMethod.PUT, request, String::class.java)

        //Verify request succeed
        Assertions.assertEquals(expectedStatus, result.statusCode)
        if(expectedErrorMessage != "")
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
            expectedErrorMessage = "createTicket.ticketPostDTO.email: The inserted input is not valid!"
        )
    }

    fun creationTicketTest(
        baseUrl: String = "http://localhost:$port/API/tickets",
        profileId: String = "this@exists.com",
        ean: String = "0000000000000",
        expectedStatus: HttpStatus,
        expectedErrorMessage: String = ""
    ) {
        val myProduct=Product(ean = "0000000000000")
        productRepository.save(myProduct)
        val myProfile=Profile(
            id = "id",
            username = "username",
            email = "this@exists.com",
            name = "this",
            surname = "exists"
        )
        profileRepository.save(myProfile)
        val myPurchase = Purchase(myProduct,myProfile,Date(),1)
        purchaseRepository.save(myPurchase)
        val myWarranty = Warranty(myPurchase,Date(),Date(2999,12,1),"type",1)
        warrantyRepository.save(myWarranty)
        val uri = URI(baseUrl)

        val ticketPost = TicketPostDTO(profileId, ean)

        val loginDTO= LoginDTO(username = "expert", password = "password")
        val token= authService.login(loginDTO)?.jwtAccessToken
        val headers = HttpHeaders()
        if (token != null) {
            headers.setBearerAuth(token)
        }
        headers.set("X-COM-PERSIST", "true")
        val request = HttpEntity(ticketPost, headers)
        val result = restTemplate.postForEntity(uri, request, String::class.java)

        //Verify request succeed
        Assertions.assertEquals(expectedStatus, result.statusCode)
        if(expectedErrorMessage != "")
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
            ean = "0000000000001",
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
            body = mapOf("expertId" to "id"),
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
    fun updateTicketExpertNonStringExpertIdTest() {
        updateTicketStatusOrPriorityLevelOrExpertIdTest(
            operationUrl = "changeExpert",
            body = mapOf("expertId" to 1),
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
            body = mapOf("expertId" to "id"),
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
            body = mapOf("expertId" to "id2"),
            expectedStatus = HttpStatus.NOT_FOUND,
            expectedErrorMessage = "Expert Not Found!"
        )
    }
}