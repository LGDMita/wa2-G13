package it.polito.wa2.g13.server.ticketing.experts


import it.polito.wa2.g13.server.ticketing.sectors.SectorNotFoundException
import it.polito.wa2.g13.server.ticketing.sectors.SectorService
import it.polito.wa2.g13.server.ticketing.sectors.SectorsNotFoundException
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@RestController
class ExpertController(
    private val expertService: ExpertService,
    private val sectorService: SectorService
) {

    //get: /API/experts
    @GetMapping("/API/experts")
    fun getMessages() : List<ExpertDTO>{
        return expertService.getExperts()
    }
    @PostMapping("/API/experts")
    @ResponseStatus(HttpStatus.CREATED)
    fun setExpert(@RequestBody @Valid expertDTO: ExpertDTO,
                  br: BindingResult): Boolean {

        if (!br.hasErrors()) {
            return if(expertService.setExpert(expertDTO))
                true
            else
                throw DuplicateExpertException()
        }
        else
            throw ExpertInvalidArgumentsException()

    }

    @GetMapping("/API/experts/{id}")
    fun getExpertById(@PathVariable id: String): ExpertDTO? {
        return expertService.getExpertById(id) ?: throw ExpertNotFoundException()
    }

    @PutMapping("/API/experts/{id}")
    fun modifyExpert(@PathVariable id: String,
                     @RequestBody @Valid expertDTO: ExpertDTO,
                     br: BindingResult) : Boolean{

        return if(!br.hasErrors()){
            val result= expertService.modifyExpert(id, expertDTO)
            if(result== 1)
                true
            else{
                if(result== 2)
                    throw ExpertNotFoundException()
                else
                    throw DuplicateExpertException()
            }
        }else
            throw ExpertInvalidArgumentsException()

    }

    @GetMapping("/API/experts/")
    fun getExpertsBySector(@RequestParam sectorName: String) : List<ExpertDTO>?{
        val sectorNameLower= sectorName.lowercase()
        val sectors= sectorService.getAllSectors()
        if(sectors!= null){
            if(sectors.find { s -> s.name== sectorNameLower } != null){
                return expertService.getExpertsBySector(sectorNameLower) ?: throw ExpertsOfSelectedSectorNotFoundException()
            }else{
                throw SectorNotFoundException()
            }
        }else{
            throw SectorsNotFoundException()
        }
    }

    @DeleteMapping("/API/experts/{id}")
    fun deleteExpertById(@PathVariable id: String) {
        if (expertService.getExpertById(id)!= null){
            return expertService.deleteExpertById(id)
        }else{
            throw ExpertNotFoundException()
        }

    }
}
