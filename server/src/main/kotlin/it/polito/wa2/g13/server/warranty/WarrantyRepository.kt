package it.polito.wa2.g13.server.warranty

import it.polito.wa2.g13.server.purchase.Purchase
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WarrantyRepository : JpaRepository<Warranty, Long> {
    fun findByPurchase(purchase: Purchase): Warranty?
}