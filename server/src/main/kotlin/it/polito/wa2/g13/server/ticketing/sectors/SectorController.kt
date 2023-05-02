package it.polito.wa2.g13.server.ticketing.sectors

import it.polito.wa2.g13.server.ticketing.experts.ExpertNotFoundException
import it.polito.wa2.g13.server.ticketing.experts.ExpertService
import jakarta.validation.Valid
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@RestController
class SectorController (
    private val sectorService: SectorService,
    private val expertService: ExpertService
){

    @GetMapping("/API/experts/sectors")
    fun getAllSectors(): List<SectorDTO>?{
        return sectorService.getAllSectors() ?: throw SectorsNotFoundException()
    }

    @GetMapping("/API/experts/{id}/sectors")
    fun getSectorsOfExpert(@PathVariable id: Long) : List<SectorDTO>?{
        if(expertService.getExpertById(id)!= null)
            return sectorService.getSectorsOfExpert(id) ?: throw SectorsNotFoundException()
        else
            throw ExpertNotFoundException()
    }

    @PostMapping("/API/experts/{id}/sectors")
    fun setSectorForExpert(@PathVariable id: Long,
                           @RequestBody @Valid sectorDTO: SectorDTO,
                           br: BindingResult) : Boolean
    {
        return if (!br.hasErrors()) {
            if(sectorService.setSectorForExpert(id, sectorDTO))
                true
            else
                throw ExpertNotFoundException()
        } else
            throw SectorInvalidArgumentException()
    }

    @DeleteMapping("/API/experts/{expertId}/sectors/{sectorId}")
    fun deleteSectorForExpert(@PathVariable expertId: Long,
                              @PathVariable sectorId: Long){
        if(expertService.getExpertById(expertId)!= null){
            sectorService.deleteSectorForExpert(expertId, sectorId)
        }else
            throw ExpertNotFoundException()

    }

}