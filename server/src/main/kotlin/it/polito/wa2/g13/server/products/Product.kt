package it.polito.wa2.g13.server.products

import it.polito.wa2.g13.server.profiles.Profile
import it.polito.wa2.g13.server.purchase.Purchase
import it.polito.wa2.g13.server.ticketing.sectors.Sector
import it.polito.wa2.g13.server.ticketing.sectors.toSector
import jakarta.persistence.*

@Entity
@Table(name="products")
class Product (
    @Id
    var ean: String = "",
    var name: String = "",
    var brand: String = "",
    @OneToOne
    @JoinColumn(name = "sector_id")
    var sector: Sector,
){
    @OneToMany(mappedBy = "product")
    val purchases: MutableSet<Purchase> = mutableSetOf()

    fun addPurchase(purch: Purchase): Unit{
        purchases.add(purch)
    }
    override fun toString(): String {
        return "ean=${ean} name=${name} brand=${brand}"
    }
}

fun ProductDTO.toProduct(): Product {
    return Product(ean, name, brand, sector.toSector())
}