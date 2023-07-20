package it.polito.wa2.g13.server.warranty

import it.polito.wa2.g13.server.EntityBase
import it.polito.wa2.g13.server.products.Product
import it.polito.wa2.g13.server.purchase.Purchase
import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "warranty")
class Warranty(
    @OneToOne
    val purchase: Purchase,
    val datetimeBuy: Date,
    val datetimeExpire: Date,
    val type: String,
    setId: Long? = null
) : EntityBase<Long>(setId)

fun WarrantyDTO.toWarranty(purch: Purchase): Warranty {
    return Warranty(purch, datetimeBuy, datetimeExpire, type, warrantyId)
}