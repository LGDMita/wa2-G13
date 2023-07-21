package it.polito.wa2.g13.server.ticketing.sectors

import it.polito.wa2.g13.server.ticketing.experts.ExpertRepository
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class SectorServiceImpl(
    private val sectorRepository: SectorRepository,
    private val expertRepository: ExpertRepository
) : SectorService {
    override fun getAllSectors(): List<SectorDTO>? {
        val listOfSectorDTOs =
            sectorRepository.findAll().map { s -> s.toDTO() }
        return listOfSectorDTOs.ifEmpty {
            null
        }
    }

    override fun getSectorsOfExpert(id: String): List<SectorDTO>? {
        val expert = expertRepository.findByIdOrNull(id)
        val listOfSectorDTOs =
            expert?.let { sectorRepository.findSectorsByExperts(it).map { s -> s.toDTO() } }
        return listOfSectorDTOs!!.ifEmpty {
            null
        }
    }

    @Transactional
    override fun setSectorForExpert(id: String, sectorDTO: SectorDTO): Boolean {
        val expert = expertRepository.findByIdOrNull(id)
        return if (expert != null) {
            if (sectorRepository.existsByName(sectorDTO.name.lowercase())) {
                val sector = sectorRepository.findByName(sectorDTO.name.lowercase())
                expert.addSector(sector)
                sector.addExpert(expert)
                sectorRepository.save(sector)
                expertRepository.save(expert)
            } else {
                val sector = sectorDTO.toSector() //generate new id
                sector.name = sector.name.lowercase() // To have all lower case names
                expert.addSector(sector)
                sector.addExpert(expert)
                sectorRepository.save(sector)
                expertRepository.save(expert)
            }
            true
        } else {
            false//Expert not found
        }
    }

    @Transactional
    override fun deleteSectorForExpert(expertId: String, sectorId: Long) {
        val expert = expertRepository.findByIdOrNull(expertId)
        if (expert != null) {
            val sectors = sectorRepository.findSectorsByExperts(expert)
            val sector = sectors.find { s -> s.getId() == sectorId }
            if (sector != null) {
                expert.removeSector(sector)
                sector.removeExpert(expert)
                sectorRepository.save(sector)
                expertRepository.save(expert)
            }
        }
    }
}