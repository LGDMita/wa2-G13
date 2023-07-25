package it.polito.wa2.g13.server.ticketing.experts


import it.polito.wa2.g13.server.ticketing.sectors.SectorRepository
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ExpertServiceImpl(
    private val expertRepository: ExpertRepository,
    private val sectorRepository: SectorRepository
) : ExpertService {

    override fun getExperts(): List<ExpertDTO> {
        return expertRepository.findAll().map { it.toDTO() }
    }

    @Transactional
    override fun setExpert(expertDTO: ExpertDTO): Boolean {

        val expert = expertDTO.toExpert()
        return if (!expertRepository.existsByEmail(expert.email)) {
            expertRepository.save(expert)
            true
        } else false

    }

    override fun getExpertById(id: String): ExpertDTO? {
        return expertRepository.findByIdOrNull(id)?.toDTO()
    }

    @Transactional
    override fun modifyExpert(id: String, expertDTO: ExpertDTO) {
        val expert = expertDTO.toExpertWithId(id)
        val sectors = expert.let { sectorRepository.findSectorsByExperts(it) }
        expertRepository.save(expert)
        val expertNew = expertRepository.findByIdOrNull(id)
        if (expertNew != null) {
            for (s in sectors) {
                expertNew.addSector(s)
                s.addExpert(expertNew)
                sectorRepository.save(s)
            }
        }
    }

    override fun getExpertsBySector(sectorName: String): List<ExpertDTO>? {

        val sector = sectorRepository.findByName(sectorName)
        val listOfExpertDTOs =
            expertRepository.findExpertsBySectors(sector).map { e -> e.toDTO() }
        return listOfExpertDTOs.ifEmpty {
            null
        }

    }

    @Transactional
    override fun deleteExpertById(id: String) {
        expertRepository.deleteById(id)
    }

}