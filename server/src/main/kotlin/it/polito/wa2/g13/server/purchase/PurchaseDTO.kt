package it.polito.wa2.g13.server.purchase

import java.util.*

data class PurchaseDTO (
    val purchaseId: Long?=null,
    val ean: String,
    val profileUsername: String?=null,
    val datetime: Date
)

fun Purchase.toDTO(): PurchaseDTO {
    return PurchaseDTO(getId(), product.ean, profile?.username, datetime)
}