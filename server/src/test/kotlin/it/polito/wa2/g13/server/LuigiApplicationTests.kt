package it.polito.wa2.g13.server

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import it.polito.wa2.g13.server.products.Product
import it.polito.wa2.g13.server.products.ProductRepository
import it.polito.wa2.g13.server.profiles.Profile
import it.polito.wa2.g13.server.profiles.ProfileRepository
import it.polito.wa2.g13.server.ticketing.experts.Expert
import it.polito.wa2.g13.server.ticketing.experts.ExpertRepository
import it.polito.wa2.g13.server.ticketing.tickets.Ticket
import it.polito.wa2.g13.server.ticketing.tickets.TicketDTO
import it.polito.wa2.g13.server.ticketing.tickets.TicketRepository
import it.polito.wa2.g13.server.ticketing.tickets.toDTO
import org.junit.FixMethodOrder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
class LuigiApplicationTests {
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
}