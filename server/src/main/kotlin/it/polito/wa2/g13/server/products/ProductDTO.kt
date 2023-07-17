package it.polito.wa2.g13.server.products

import it.polito.wa2.g13.server.ticketing.sectors.SectorDTO
import it.polito.wa2.g13.server.ticketing.sectors.toDTO

data class ProductDTO(
    val ean: String,
    val name: String,
    val brand: String,
    val sector: SectorDTO
)

fun Product.toDTO(): ProductDTO{
    return ProductDTO(ean, name, brand, sector.toDTO())
}
