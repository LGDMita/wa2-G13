package it.polito.wa2.g13.server.purchase

import it.polito.wa2.g13.server.jwtAuth.JwtAuthConverter
import it.polito.wa2.g13.server.profiles.InvalidArgumentsException
import it.polito.wa2.g13.server.profiles.ProfileService
import jakarta.validation.constraints.NotBlank
import org.jetbrains.annotations.NotNull
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
    @GetMapping("/API/customer/purchases")
    fun getPurchasesOf(): List<PurchaseWithWarrantyDTO> {
        println("Qui ci sono")
        return purchaseService.getPurchasesOf(SecurityContextHolder.getContext().authentication.name)
    }
}