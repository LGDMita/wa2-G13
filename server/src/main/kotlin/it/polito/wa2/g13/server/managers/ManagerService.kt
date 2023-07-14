package it.polito.wa2.g13.server.managers

interface ManagerService {
    fun getManager(id: String): ManagerDTO?

    fun modifyManager(id: String, managerDTO: ManagerDTO)

}