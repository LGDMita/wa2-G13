package it.polito.wa2.g13.server.ticketing.experts


import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g13.server.jwtAuth.AuthService
import it.polito.wa2.g13.server.ticketing.sectors.SectorNotFoundException
import it.polito.wa2.g13.server.ticketing.sectors.SectorService
import it.polito.wa2.g13.server.ticketing.sectors.SectorsNotFoundException
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@RestController
@Observed
@Slf4j
class ExpertController(
    private val expertService: ExpertService,
    private val sectorService: SectorService,
    private val authService: AuthService
) {

    private val log = LoggerFactory.getLogger(ExpertController::class.java)

    //get: /API/experts
    @GetMapping("/API/experts")
    fun getExperts() : List<ExpertDTO>{
        log.info("Getting all experts")
        return expertService.getExperts()
    }

    //No longer necessary as creation managed with keycloak
    /*
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
    */

    @GetMapping("/API/experts/{id}")
    fun getExpertById(@PathVariable id: String): ExpertDTO? {
        log.info("Getting expert with id:{}", id)
        return expertService.getExpertById(id) ?: throw ExpertNotFoundException()
    }

    //Must be modified or moved to pass the changes to keycloak as well
    @Transactional
    @PutMapping("/API/experts/{id}")
    fun modifyExpert(@PathVariable id: String,
                     @RequestBody @Valid expertDTO: ExpertDTO,
                     br: BindingResult) : Boolean{
        return if(!br.hasErrors()){
            val oldExpert= expertService.getExpertById(id)?.toRegisterDTO()?:throw ExpertNotFoundException()
            authService.updateUser(id,oldExpert,expertDTO.toRegisterDTO())
            expertService.modifyExpert(id, expertDTO)
            true
        }else
            throw ExpertInvalidArgumentsException()
    }

    @GetMapping("/API/experts/")
    fun getExpertsBySector(@RequestParam sectorName: String) : List<ExpertDTO>?{
        val sectorNameLower= sectorName.lowercase()
        val sectors= sectorService.getAllSectors()
        if(sectors!= null){
            if(sectors.find { s -> s.name== sectorNameLower } != null){
                log.info("Getting expert by sector name:{}", sectorName)
                return expertService.getExpertsBySector(sectorNameLower) ?: throw ExpertsOfSelectedSectorNotFoundException()
            }else{
                log.warn("No sector found with name:{}", sectorName)
                throw SectorNotFoundException()
            }
        }else{
            log.warn("No sector found with name:{}", sectorName)
            throw SectorsNotFoundException()
        }
    }

    @Transactional
    @DeleteMapping("/API/experts/{id}")
    fun deleteExpertById(@PathVariable id: String) {
        println("Deleting $id expert!")
        expertService.getExpertById(id)?:throw ExpertNotFoundException()
        authService.deleteUser(id)
        expertService.deleteExpertById(id)
    }
}
