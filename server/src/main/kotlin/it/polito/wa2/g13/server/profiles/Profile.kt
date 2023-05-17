package it.polito.wa2.g13.server.profiles

import it.polito.wa2.g13.server.EntityBase
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name= "profiles")
class Profile(
    var email: String,
    var name: String,
    var surname: String,
    setId: Long?=null) : EntityBase<Long>(setId) {

}

fun ProfileDTO.toProfile(): Profile {
    return Profile(email, name, surname, id)
}