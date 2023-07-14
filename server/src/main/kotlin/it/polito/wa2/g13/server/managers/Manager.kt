package it.polito.wa2.g13.server.managers

import jakarta.persistence.*

@Entity
@Table(name = "managers")
class Manager(
    @Id
    @Column(updatable = false, nullable = false)
    var id: String,
    var username: String,
    var email: String,
    var name: String,
    var surname: String
)

fun ManagerDTO.toManager(): Manager {
    return Manager(id, username, email, name, surname)
}