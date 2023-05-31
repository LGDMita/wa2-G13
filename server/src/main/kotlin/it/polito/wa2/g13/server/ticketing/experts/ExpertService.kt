package it.polito.wa2.g13.server.ticketing.experts

import org.springframework.stereotype.Service

@Service
interface ExpertService {
    fun setExpert(expertDTO: ExpertDTO): Boolean

    fun getExpertById(id: String): ExpertDTO?

    fun modifyExpert(id: String, expertDTO: ExpertDTO): Unit

    fun getExpertsBySector(sectorName: String): List<ExpertDTO>?

    fun deleteExpertById(id: String)

    fun getExperts() : List<ExpertDTO>
}