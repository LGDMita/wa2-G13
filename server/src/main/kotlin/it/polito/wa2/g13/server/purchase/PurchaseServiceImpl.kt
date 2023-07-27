package it.polito.wa2.g13.server.purchase

import it.polito.wa2.g13.server.profiles.ProfileNotFoundException
import it.polito.wa2.g13.server.profiles.ProfileRepository
import it.polito.wa2.g13.server.ticketing.tickets.TicketRepository
import it.polito.wa2.g13.server.warranty.WarrantyRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class PurchaseServiceImpl(
    private val purchaseRepository: PurchaseRepository,
    private val profileRepository: ProfileRepository,
    private val warrantyRepository: WarrantyRepository
) : PurchaseService {
    override fun getPurchasesOf(username: String): List<PurchaseWithWarrantyDTO> {
        val profile = profileRepository.findProfileByUsername(username) ?: throw ProfileNotFoundException()
        return purchaseRepository.findAllByProfile(profile).map { it.toPWWDTO(warrantyRepository.findByPurchase(it)) }
    }

    override fun deleteCustomer(customerId: String): Boolean {
        val customer = profileRepository.findByIdOrNull(customerId) ?: throw ProfileNotFoundException()

        purchaseRepository.clearProfileIdForPurchaseWithProfiles(customer)

        return true
    }
}