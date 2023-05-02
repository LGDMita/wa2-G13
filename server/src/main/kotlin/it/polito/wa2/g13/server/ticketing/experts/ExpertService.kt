package it.polito.wa2.g13.server.ticketing.experts

import org.springframework.stereotype.Service

@Service
interface ExpertService {
    fun setExpert(expertDTO: ExpertDTO): Boolean

    fun getExpertById(id: Long): ExpertDTO?

    fun modifyExpert(id: Long, expertDTO: ExpertDTO): Int

    fun getExpertsBySector(sectorName: String): List<ExpertDTO>?

    fun deleteExpertById(id: Long)
}