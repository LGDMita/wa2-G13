package it.polito.wa2.g13.server.profiles

import it.polito.wa2.g13.server.purchase.Purchase
import jakarta.persistence.*

@Entity
@Table(name = "profiles")
class Profile(
    @Id
    @Column(updatable = false, nullable = false)
    var id: String,
    var username: String,
    var email: String,
    var name: String,
    var surname: String
) {
    @OneToMany(mappedBy = "profile")
    val purchases: MutableSet<Purchase> = mutableSetOf()
    fun addPurchase(purch: Purchase): Unit {
        purchases.add(purch)
        purch.profile = this
    }

    fun removePurchase(purch: Purchase): Unit {
        purchases.remove(purch)
        purch.profile = null
    }
}

fun ProfileDTO.toProfile(): Profile {
    return Profile(id, username, email, name, surname)
}