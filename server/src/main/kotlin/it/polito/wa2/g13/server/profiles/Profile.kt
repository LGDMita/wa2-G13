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
}