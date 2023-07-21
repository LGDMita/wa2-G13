package it.polito.wa2.g13.server.purchase

import it.polito.wa2.g13.server.EntityBase
import it.polito.wa2.g13.server.products.Product
import it.polito.wa2.g13.server.profiles.Profile
import it.polito.wa2.g13.server.warranty.Warranty
import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "purchase")
class Purchase(
    @ManyToOne
    val product: Product,
    @ManyToOne
    var profile: Profile?,
    val datetime: Date,
    setId: Long? = null
) : EntityBase<Long>(setId) {
    @OneToOne(mappedBy = "purchase")
    val warranty: Warranty? = null
}

fun PurchaseDTO.toPurchase(prod: Product, prof: Profile?): Purchase {
    return Purchase(prod, prof, datetime, purchaseId)
}