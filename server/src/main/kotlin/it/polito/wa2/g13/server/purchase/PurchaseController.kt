package it.polito.wa2.g13.server.purchase

import it.polito.wa2.g13.server.jwtAuth.JwtAuthConverter
import it.polito.wa2.g13.server.profiles.InvalidArgumentsException
import it.polito.wa2.g13.server.profiles.ProfileController
import it.polito.wa2.g13.server.profiles.ProfileService
import jakarta.validation.constraints.NotBlank
import org.jetbrains.annotations.NotNull
import org.slf4j.LoggerFactory
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.nio.file.attribute.UserPrincipalNotFoundException

@RestController
class PurchaseController(
    private val purchaseService: PurchaseService,
    private val profileService: ProfileService,
    private val jwtAuthConverter: JwtAuthConverter
) {

    private val log = LoggerFactory.getLogger(ProfileController::class.java)

    @GetMapping("/API/customer/purchases")
    fun getPurchasesOf(): List<PurchaseWithWarrantyDTO> {
        val name = SecurityContextHolder.getContext().authentication.name;
        log.info("Returning user purchases. Username: {}", name)
        return purchaseService.getPurchasesOf(name)
    }
}