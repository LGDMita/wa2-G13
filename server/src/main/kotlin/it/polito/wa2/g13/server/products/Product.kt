package it.polito.wa2.g13.server.products

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name="products")
class Product {
    @Id
    var ean: String = ""
    var name: String = ""
    var brand: String = ""

    override fun toString(): String {
        return "ean=${ean} name=${name} brand=${brand}"
    }
}