package it.polito.wa2.g13.server.ticketing.sectors

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g13.server.jwtAuth.AuthController
import it.polito.wa2.g13.server.ticketing.experts.ExpertNotFoundException
import it.polito.wa2.g13.server.ticketing.experts.ExpertService
import jakarta.validation.Valid
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@RestController
@Observed
@Slf4j
class SectorController (
    private val sectorService: SectorService,
    private val expertService: ExpertService
){

    private val log = LoggerFactory.getLogger(AuthController::class.java)

    @GetMapping("/API/experts/sectors")
    fun getAllSectors(): List<SectorDTO>?{
        log.info("Requiring all sectors")
        return sectorService.getAllSectors() ?: throw SectorsNotFoundException()
    }

    @GetMapping("/API/experts/{id}/sectors")
    fun getSectorsOfExpert(@PathVariable id: String) : List<SectorDTO>?{
        if(expertService.getExpertById(id)!= null) {
            log.info("Requiring services of expert with id: {}", id)
            return sectorService.getSectorsOfExpert(id) ?: throw ExpertSectorsNotFoundException()
        }
        else {
            log.warn("No exert found with id: {}", id)
            throw ExpertNotFoundException()
        }
    }

    @PostMapping("/API/experts/{id}/sectors")
    fun setSectorForExpert(@PathVariable id: String,
                           @RequestBody @Valid sectorDTO: SectorDTO,
                           br: BindingResult) : Boolean
    {
        return if (!br.hasErrors()) {
            if(sectorService.setSectorForExpert(id, sectorDTO)) {
                log.info("Secotr:{} succesfully setted for expert with id:{}", sectorDTO.toString(), id)
                true
            }
            else {
                log.warn("No expert found for id: {}", id)
                throw ExpertNotFoundException()
            }
        } else {
            log.warn("Filed constraint not satisfied for DTO: {}", sectorDTO.toString())
            throw SectorInvalidArgumentException()
        }
    }

    @DeleteMapping("/API/experts/{expertId}/sectors/{sectorId}")
    fun deleteSectorForExpert(@PathVariable expertId: String,
                              @PathVariable sectorId: Long){

        if(expertService.getExpertById(expertId)!= null){
            val sectors= sectorService.getAllSectors()
            if(sectors!= null){
                if(sectors.find { s -> s.sectorId== sectorId } != null){
                    val expertSectors= sectorService.getSectorsOfExpert(expertId)
                    if(expertSectors!= null){
                        if(expertSectors.find { s -> s.sectorId== sectorId } != null) {
                            log.info("Deleting sector with id:{} for expert with id:{}", sectorId, expertId)
                            sectorService.deleteSectorForExpert(expertId, sectorId)
                        }
                        else {
                            log.warn("No expert found with id:{}", expertId)
                            throw ExpertSectorNotFoundException()
                        }
                    } else {
                        log.warn("Expert with id:{} has no sector with id:{}", expertId, sectorId)
                        throw ExpertSectorsNotFoundException()
                    }
                }else {
                    log.warn("No sector found with id:{}", sectorId)
                    throw SectorNotFoundException()
                }
            }else {
                log.warn("No sector found with id:{}", sectorId)
                throw SectorsNotFoundException()
            }
        }else {
            log.warn("No expert found with id:{}", expertId)
            throw ExpertNotFoundException()
        }
    }

}