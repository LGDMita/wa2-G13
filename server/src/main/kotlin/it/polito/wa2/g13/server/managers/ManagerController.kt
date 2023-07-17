package it.polito.wa2.g13.server.managers

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g13.server.jwtAuth.AuthService
import it.polito.wa2.g13.server.profiles.*
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@Validated
@Observed
@Slf4j
class ManagerController(
    private val managerService: ManagerService,
    private val authService: AuthService,
) {

    private val log = LoggerFactory.getLogger(ProfileController::class.java)

    @GetMapping("/API/managers/{id}")
    fun getProfile(@PathVariable id: String): ManagerDTO? {
        log.info("Request manager with id: {}", id)
        return managerService.getManager(id) ?: throw ManagerNotFoundException()
    }

    @Transactional
    @PutMapping("/API/managers/{id}")
    fun modifyProfile(
        @PathVariable id: String,
        @RequestBody @Valid managerDTO: ManagerDTO,
        br: BindingResult
    ): Boolean {
        log.info("Edited manager with id: {} and managerDTO: {}", id, managerDTO.toString())
        return if (!br.hasErrors()) {
            val oldProfile=managerService.getManager(id)?.toRegisterDTO()?:throw ManagerNotFoundException()
            authService.updateUser(id, oldProfile, managerDTO.toRegisterDTO())
            managerService.modifyManager(id, managerDTO)
            true
        } else {
            log.info("Filed constraint not satisfied for DTO: {}", managerDTO.toString())
            throw InvalidArgumentsException()
        }
    }
}