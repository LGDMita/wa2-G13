package it.polito.wa2.g13.server.ticketing.tickets

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g13.server.jwtAuth.AuthController
import it.polito.wa2.g13.server.profiles.ProfileService
import jakarta.validation.Valid
import jakarta.validation.constraints.*
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.security.Principal
import java.util.*


@RestController
@Observed
@Slf4j
class TicketController(
    private val ticketService: TicketService
) {
    private val log = LoggerFactory.getLogger(TicketController::class.java)

    @GetMapping("/API/tickets")
    fun getTickets(): List<Ticket>? {
        log.info("Request all tickets")
        return ticketService.getTickets()
    }

    @GetMapping("/API/tickets/{ticketId}")
    fun getTicket(@PathVariable("ticketId") ticketId: Long): TicketDTO? {
        log.info("Request specific ticket with id: {}", ticketId)
        return ticketService.getTicket(ticketId) ?: throw TicketNotFoundException()
    }

    @PutMapping("/API/ticket/")
    fun modifyTicket(
        @RequestBody @Valid ticketDTO: TicketDTO,
        br: BindingResult
    ): Boolean {
        if (!br.hasErrors()) {
            if (ticketService.getTicket(ticketDTO.ticketId!!) != null) {
                log.info("Editing ticket: {}", ticketDTO.toString())
                return ticketService.modifyTicket(ticketDTO)
            }
            else {
                log.info("Edit ticket, not ticket found with id: {}", ticketDTO.ticketId)
                throw TicketNotFoundException()
            }
        }
        else {
            log.warn("Filed constraint not satisfied for DTO: {}", ticketDTO.toString())
            throw InvalidTicketException()
        }
    }
}

@RestController
@Validated
@Observed
@Slf4j
class TicketControllerValidated(
    private val ticketService: TicketService,
    private val profileService: ProfileService,
) {

    private val log = LoggerFactory.getLogger(AuthController::class.java)

    @GetMapping("/API/tickets/test/")
    fun getTest(principal: Principal): ResponseEntity<String?>? {
        val token = principal as JwtAuthenticationToken
        val userName = token.tokenAttributes["name"] as String?
        val userEmail = token.tokenAttributes["email"] as String?
        return ResponseEntity.ok("Hello! \nUser Name : $userName\nUser Email : $userEmail")
    }


    @GetMapping("/API/tickets/")
    fun getFilteredTickets(
        @RequestParam("ean")
        @Size(min=1, max=15, message = "Ean MUST be a NON empty string of max 15 chars")
        ean: String?,
        @RequestParam("profileId")
        profileId: String?,
        @RequestParam("priorityLevel")
        @Min(value = 0, message = "Minimum value for priorityLevel is 0")
        @Max(value = 4, message = "Minimum value for priorityLevel is 4")
        priorityLevel: Int?,
        @RequestParam("expertId")
        expertId: String?,
        @RequestParam("status")
        @Size(min=1, max=15, message = "Status MUST be a NON empty string of max 15 chars")
        @Pattern(regexp = "(open|closed|resolved|in_progress|reopened)")
        status: String?,
        @RequestParam("creationDateStart")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        creationDateStart: Date?,
        @RequestParam("creationDateStop")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        creationDateStop: Date?,
    ): List<TicketDTO> {
        log.info("Get ticket filtered by ean:{} profileId:{} priorityLevel:{} expertId:{} status:{} creationDateStart:{} creationDateStop:{}", ean, profileId, priorityLevel, expertId, status, creationDateStart, creationDateStop)
        return ticketService.getFilteredTickets(
            ean,
            profileId,
            priorityLevel,
            expertId,
            status,
            creationDateStart,
            creationDateStop
        )
    }
    @PostMapping("/API/tickets")
    @ResponseStatus(HttpStatus.CREATED)
    fun createTicket(
        @Valid @RequestBody(required = true) ticketPostDTO: TicketPostDTO,
        br: BindingResult
    ): TicketDTO? {
        if (!br.hasErrors()) {
            log.info("Creating ticket: {}", ticketPostDTO.toString())
            return ticketService.createTicket(ticketPostDTO, SecurityContextHolder.getContext().authentication.name, br)
        }
        else {
            log.warn("Filed constraint not satisfied for DTO: {}", ticketPostDTO.toString())
            throw InvalidTicketArgumentsException()
        }
    }

    @PutMapping("/API/tickets/{ticketId}/changeStatus")
    fun changeStatus(
        @PathVariable ticketId: Int,
        @RequestBody req: Map<String, Any>
    ): Boolean {
        val status = req["status"] ?: throw InvalidTicketArgumentsException()

        if(status !is String || !listOf("open", "in_progress", "reopened", "resolved", "closed").contains(status)) {
            log.warn("Ticket status not valid")
            throw InvalidTicketArgumentsException()
        }

        log.info("Changing status of ticket  with id:{} to {}", ticketId, status)

        return ticketService.changeStatus(ticketId.toLong(), status)
    }

    @PutMapping("/API/tickets/{ticketId}/changePriority")
    fun changePriority(
        @PathVariable ticketId: Int,
        @RequestBody req: Map<String, Any>
    ): Boolean {
        val priorityLevel = req["priorityLevel"] ?: throw InvalidTicketArgumentsException()

        if(priorityLevel !is Int || priorityLevel > 4 || priorityLevel < 0){
            log.warn("Ticket priorityLevel not valid")
            throw InvalidTicketArgumentsException()
        }

        log.info("Changing priority level of ticket with id:{} to {}", ticketId, priorityLevel)

        return ticketService.changePriority(ticketId.toLong(), priorityLevel)
    }

    @PutMapping("/API/tickets/{ticketId}/changeExpert")
    fun changeExpert(
        @PathVariable ticketId: Int,
        @RequestBody req: Map<String, Any>
    ): Boolean {
        val expertId = req["expertId"] ?: throw InvalidTicketArgumentsException()

        if(expertId !is String){
            log.warn("Ticket expertId not valid")
            throw InvalidTicketArgumentsException()
        }

        log.info("Changing expert assigned to ticket with id: {} to {}", ticketId, expertId)

        return ticketService.changeExpert(ticketId.toLong(), expertId)
    }
}
