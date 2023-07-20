package it.polito.wa2.g13.server.ticketing.sectors

import org.springframework.stereotype.Service


@Service
interface SectorService {
    fun getAllSectors(): List<SectorDTO>?

    fun getSectorsOfExpert(id: String): List<SectorDTO>?

    fun setSectorForExpert(id: String, sectorDTO: SectorDTO): Boolean

    fun deleteSectorForExpert(expertId: String, sectorId: Long)
}