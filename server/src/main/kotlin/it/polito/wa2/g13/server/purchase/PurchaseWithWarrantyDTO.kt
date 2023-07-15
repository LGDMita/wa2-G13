package it.polito.wa2.g13.server.purchase

import it.polito.wa2.g13.server.products.ProductDTO
import it.polito.wa2.g13.server.products.toDTO
import it.polito.wa2.g13.server.warranty.Warranty
import it.polito.wa2.g13.server.warranty.WarrantyDTO
import it.polito.wa2.g13.server.warranty.toDTO
import java.util.*

data class PurchaseWithWarrantyDTO(
    val purchaseId: Long?=null,
    val product: ProductDTO,
    val profileId: String?=null,
    val datetime: Date,
    val warranty: WarrantyDTO?,
)

fun Purchase.toPWWDTO(warranty: Warranty?): PurchaseWithWarrantyDTO{
    return PurchaseWithWarrantyDTO(getId(), product.toDTO(), profile?.id, datetime,warranty?.toDTO())
}