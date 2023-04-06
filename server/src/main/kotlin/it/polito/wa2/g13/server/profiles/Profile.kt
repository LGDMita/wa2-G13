package it.polito.wa2.g13.server.profiles

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name= "profiles")
class Profile {
    @Id
    var email= ""
    var name= ""
    var surname= ""

    constructor(email: String, name: String, surname: String) {
        this.email = email
        this.name = name
        this.surname = surname
    }


}