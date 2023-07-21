package it.polito.wa2.g13.server.warranty

import java.util.Date

data class WarrantyDTO(
    val warrantyId: Long? = null,
    val purchaseId: Long,
    val datetimeBuy: Date,
    val datetimeExpire: Date,
    val type: String
)

fun Warranty.toDTO(): WarrantyDTO {
    return WarrantyDTO(getId(), purchase.getId()!!, datetimeBuy, datetimeExpire, type)
}