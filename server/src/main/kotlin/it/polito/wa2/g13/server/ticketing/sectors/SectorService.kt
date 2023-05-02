package it.polito.wa2.g13.server.ticketing.sectors

import org.springframework.stereotype.Service


@Service
interface SectorService {
    fun getAllSectors() : List<SectorDTO>?

    fun getSectorsOfExpert(id: Long) : List<SectorDTO>?

    fun setSectorForExpert(id: Long, sectorDTO: SectorDTO) : Boolean

    fun deleteSectorForExpert(expertId: Long, sectorId: Long)
}