package it.polito.wa2.g13.server.ticketing.experts


import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service


@Service
class ExpertServiceImpl(
    private val expertRepository: ExpertRepository
) : ExpertService {

    override fun setExpert(expertDTO: ExpertDTO): Boolean {

        val expert= expertDTO.toExpert()
        return if (!expertRepository.existsByEmail(expert.email)){
            expertRepository.save(expert)
            true
        }else false

    }

    override fun getExpertById(id: Long): ExpertDTO?{
        return expertRepository.findByIdOrNull(id.toString())?.toDTO()
    }

    override fun modifyExpert(id: Long, expertDTO: ExpertDTO): Int {
        return if(expertRepository.existsById(id.toString())){
            if(expertRepository.existsByEmail(expertDTO.toExpert().email)){
                0 // DuplicateExpertException
            }else{
                expertRepository.save(expertDTO.toExpert())
                1 // Ok
            }
        }else{
            2 // ExpertNotFoundException
        }
    }

    override fun getExpertsBySector(sector: String): List<ExpertDTO>? {

        val listOfExperts= expertRepository.findBySector(sector)
        return if (listOfExperts.isEmpty()){
            null
        }else{
            val listOfExpertDTOs= mutableListOf<ExpertDTO>()
            for(e in listOfExperts){
                listOfExpertDTOs.add(e.toDTO())
            }
            listOfExpertDTOs
        }

    }

    override fun deleteExpertById(id: Long){
        expertRepository.deleteById(id.toString())
    }

}