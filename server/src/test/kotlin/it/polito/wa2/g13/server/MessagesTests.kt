package it.polito.wa2.g13.server

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import it.polito.wa2.g13.server.jwtAuth.AuthService
import it.polito.wa2.g13.server.jwtAuth.LoginDTO
import it.polito.wa2.g13.server.products.Product
import it.polito.wa2.g13.server.products.ProductRepository
import it.polito.wa2.g13.server.profiles.Profile
import it.polito.wa2.g13.server.profiles.ProfileRepository
import it.polito.wa2.g13.server.ticketing.attachments.Attachment
import it.polito.wa2.g13.server.ticketing.attachments.AttachmentRepository
import it.polito.wa2.g13.server.ticketing.experts.Expert
import it.polito.wa2.g13.server.ticketing.experts.ExpertRepository
import it.polito.wa2.g13.server.ticketing.messages.Message
import it.polito.wa2.g13.server.ticketing.messages.MessageDTO
import it.polito.wa2.g13.server.ticketing.messages.MessageRepository
import it.polito.wa2.g13.server.ticketing.tickets.Ticket
import it.polito.wa2.g13.server.ticketing.tickets.TicketRepository
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
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.net.URI
import java.util.*


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) // force name ordering
class MessagesTests {
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
    private val webApplicationContext: WebApplicationContext? = null

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
    lateinit var messageRepository: MessageRepository

    @Autowired
    lateinit var attachmentRepository: AttachmentRepository

    @Autowired
    lateinit var authService: AuthService

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun t1TestGetMessage(){
        val baseUrl = "http://localhost:$port/API/tickets/1/messages"
        val uri = URI(baseUrl)
        val myProfile = Profile(
            id = "id",
            username = "moyne",
            email = "moyne@gmail.com",
            name = "Mohamed Amine",
            surname = "Hamdi"
        )
        val myProduct = Product(
            "4935531465706",
            "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
            "JMT"
        )
        val myExpert = Expert(
            id = "id",
            username = "giomalnati",
            name = "Giovanni",
            surname = "Malnati",
            email = "giovanni.malnati@polito.it"
        )
        val myTicket = Ticket(
            profile = myProfile, product = myProduct, priorityLevel = 1, expert = myExpert,
            status = "open", creationDate = Date(), messages = mutableSetOf()
        )
        val myMessage = Message(
            myTicket,true,"Hey I'm Test Profile, I'm having issues with my new product!",
            Date(), mutableSetOf()
        )

        profileRepository.save(myProfile)
        productRepository.save(myProduct)
        expertRepository.save(myExpert)
        ticketRepository.save(myTicket)
        messageRepository.save(myMessage)

        val loginDTO= LoginDTO(username = "expert", password = "password")
        val token= authService.login(loginDTO)?.jwtAccessToken
        val headers = HttpHeaders()
        if (token != null) {
            headers.setBearerAuth(token)
        }
        headers.set("X-COM-PERSIST", "true")
        val request = HttpEntity(null, headers)
        val result = restTemplate.exchange(uri, HttpMethod.GET, request, String::class.java)


        Assertions.assertEquals(HttpStatus.OK, result.statusCode)

        messageRepository.delete(myMessage)
        ticketRepository.delete(myTicket)
        profileRepository.delete(myProfile)
        productRepository.delete(myProduct)
        expertRepository.delete(myExpert)
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun t2TestGetMessageWithAttachment(){
        val baseUrl = "http://localhost:$port/API/tickets/1/messages"
        val uri = URI(baseUrl)
        val myProfile = Profile(
            id = "id",
            username = "moyne",
            email = "moyne@gmail.com",
            name = "Mohamed Amine",
            surname = "Hamdi"
        )
        val myProduct = Product(
            "4935531465706",
            "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
            "JMT"
        )
        val myExpert = Expert(
            id = "id",
            username = "giomalnati",
            name = "Giovanni",
            surname = "Malnati",
            email = "giovanni.malnati@polito.it"
        )
        val myTicket = Ticket(
            profile = myProfile, product = myProduct, priorityLevel = 1, expert = myExpert,
            status = "open", creationDate = Date(), messages = mutableSetOf()
        )
        val myMessage = Message(
            myTicket,true,"Hey I'm Test Profile, I'm having issues with my new product!",
            Date(), mutableSetOf()
        )
        val myMessage2 = Message(
            myTicket,false, "Hey I'm expert x tell me what's the issue",Date(), mutableSetOf()
        )

        profileRepository.save(myProfile)
        productRepository.save(myProduct)
        expertRepository.save(myExpert)
        ticketRepository.save(myTicket)
        messageRepository.save(myMessage)
        messageRepository.save(myMessage2)

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
        val arrayMessagesType = object : TypeToken<List<Message>>() {}.type
        val messages: List<MessageDTO> = gson.fromJson(result.body, arrayMessagesType)

        Assertions.assertEquals(HttpStatus.OK, result.statusCode)
        Assertions.assertEquals(messages.size,2)

        messageRepository.delete(myMessage2)
        messageRepository.delete(myMessage)
        ticketRepository.delete(myTicket)
        profileRepository.delete(myProfile)
        productRepository.delete(myProduct)
        expertRepository.delete(myExpert)
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun t3TestGetMessageWithAttachment(){
        val baseUrl = "http://localhost:$port/API/tickets/1/messages"
        val uri = URI(baseUrl)
        val myProfile = Profile(
            id = "id",
            username = "moyne",
            email = "moyne@gmail.com",
            name = "Mohamed Amine",
            surname = "Hamdi"
        )
        val myProduct = Product(
            "4935531465706",
            "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
            "JMT"
        )
        val myExpert = Expert(
            id = "id",
            username = "giomalnati",
            name = "Giovanni",
            surname = "Malnati",
            email = "giovanni.malnati@polito.it"
        )
        val myTicket = Ticket(
            profile = myProfile, product = myProduct, priorityLevel = 1, expert = myExpert,
            status = "open", creationDate = Date(), messages = mutableSetOf()
        )
        val myAttachment=Attachment(null,"image/png",1, byteArrayOf(3),Date())
        val myMessage = Message(
            myTicket,true,"Hey I'm Test Profile, I'm having issues with my new product!",
            Date(), mutableSetOf(myAttachment)

        )
        profileRepository.save(myProfile)
        productRepository.save(myProduct)
        expertRepository.save(myExpert)
        ticketRepository.save(myTicket)
        messageRepository.save(myMessage)

        val loginDTO= LoginDTO(username = "expert", password = "password")
        val token= authService.login(loginDTO)?.jwtAccessToken
        val headers = HttpHeaders()
        if (token != null) {
            headers.setBearerAuth(token)
        }
        headers.set("X-COM-PERSIST", "true")
        val request = HttpEntity(null, headers)
        val result = restTemplate.exchange(uri, HttpMethod.GET, request, String::class.java)

        Assertions.assertEquals(HttpStatus.OK, result.statusCode)
        println("Body --> ${result.body}")

        attachmentRepository.delete(myAttachment)
        messageRepository.delete(myMessage)
        ticketRepository.delete(myTicket)
        profileRepository.delete(myProfile)
        productRepository.delete(myProduct)
        expertRepository.delete(myExpert)
    }
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun t4GetButTicketDoesntExist(){
        val baseUrl = "http://localhost:$port/API/tickets/100/messages"
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
        Assertions.assertEquals(result.statusCode,HttpStatus.NOT_FOUND)
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun t5PostButTicketDoesntExist() {
        val baseUrl = "http://localhost:$port/API/tickets/100/messages"
        val uri = URI(baseUrl)
        val file = MockMultipartFile(
            "attachments",
            "hello.png",
            MediaType.IMAGE_PNG_VALUE,
            "Hello, World!".toByteArray()
        )

        val mockMvc = webApplicationContext?.let { MockMvcBuilders.webAppContextSetup(it).build() }

        val loginDTO= LoginDTO(username = "expert", password = "password")
        val token= authService.login(loginDTO)?.jwtAccessToken
        val requestBuilder = multipart(uri)
            .file(file)
            .param("fromUser", "False")
            .param("text", "....")
            .header("Authorization", "Bearer $token")

        mockMvc?.perform(requestBuilder)
            ?.andExpect(status().isNotFound())
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    fun t6PostMessage() {
        val baseUrl = "http://localhost:$port/API/tickets/1/messages"
        val uri = URI(baseUrl)
        val myProfile = Profile(
            id = "id",
            username = "moyne",
            email = "moyne@gmail.com",
            name = "Mohamed Amine",
            surname = "Hamdi"
        )
        val myProduct = Product(
            "4935531465706",
            "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
            "JMT"
        )
        val myExpert = Expert(
            id = "id",
            username = "giomalnati",
            name = "Giovanni",
            surname = "Malnati",
            email = "giovanni.malnati@polito.it"
        )
        val myTicket = Ticket(
            profile = myProfile, product = myProduct, priorityLevel = 1, expert = myExpert,
            status = "open", creationDate = Date(), messages = mutableSetOf()
        )
        profileRepository.save(myProfile)
        productRepository.save(myProduct)
        expertRepository.save(myExpert)
        ticketRepository.save(myTicket)

        val file = MockMultipartFile(
            "attachments",
            "hello.png",
            MediaType.IMAGE_PNG_VALUE,
            "Hello, World!".toByteArray()
        )

        val mockMvc = webApplicationContext?.let { MockMvcBuilders.webAppContextSetup(it).build() }

        val loginDTO= LoginDTO(username = "expert", password = "password")
        val token= authService.login(loginDTO)?.jwtAccessToken
        val requestBuilder = multipart(uri)
            .file(file)
            .param("fromUser", "False")
            .param("text", "....")
            .header("Authorization", "Bearer $token")

        mockMvc?.perform(requestBuilder)
            ?.andExpect(status().isOk())
    }

}