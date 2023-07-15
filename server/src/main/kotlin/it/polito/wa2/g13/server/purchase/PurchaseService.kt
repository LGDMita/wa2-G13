package it.polito.wa2.g13.server.purchase

interface PurchaseService {

    fun getPurchasesOf(username: String): List<PurchaseWithWarrantyDTO>
}