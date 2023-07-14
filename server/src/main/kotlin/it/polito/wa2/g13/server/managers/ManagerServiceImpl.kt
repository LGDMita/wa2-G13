package it.polito.wa2.g13.server.managers

import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
@Service
class ManagerServiceImpl (
    private val managerRepository: ManagerRepository
) : ManagerService {
    override fun getManager(id: String): ManagerDTO? {
        return managerRepository.findByIdOrNull(id)?.toDTO()
    }

    @Transactional
    override fun modifyManager(id: String, managerDTO: ManagerDTO) {
        managerRepository.save(managerDTO.toManager())
    }

}