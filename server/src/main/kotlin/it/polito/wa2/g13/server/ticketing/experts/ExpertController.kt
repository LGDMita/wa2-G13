package it.polito.wa2.g13.server.ticketing.experts


import jakarta.validation.Valid
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@RestController
class ExpertController(
    private val expertService: ExpertService
) {

    //get: /API/experts
    @GetMapping("/API/experts")
    fun getMessages() : List<ExpertDTO>{
        return expertService.getExperts()
    }
    @PostMapping("/API/experts")
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
    fun getExpertById(@PathVariable id: Long): ExpertDTO? {
        return expertService.getExpertById(id) ?: throw ExpertNotFoundException()
    }

    @PutMapping("/API/experts/{id}")
    fun modifyExpert(@PathVariable id: Long,
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
        return expertService.getExpertsBySector(sectorName) ?: throw ExpertsOfSelectedSectorNotFoundException()
    }

    @DeleteMapping("/API/experts/{id}")
    fun deleteExpertById(@PathVariable id: Long) {
        if (expertService.getExpertById(id)!= null){
            return expertService.deleteExpertById(id)
        }else{
            throw ExpertNotFoundException()
        }

    }
}
