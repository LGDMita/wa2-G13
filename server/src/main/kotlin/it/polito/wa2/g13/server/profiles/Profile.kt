package it.polito.wa2.g13.server.profiles

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name= "profiles")
class Profile(@Id var email: String, var name: String, var surname: String) {

    override fun toString(): String {
        return "email=${email} name=${name} surname=${surname}"
    }
}