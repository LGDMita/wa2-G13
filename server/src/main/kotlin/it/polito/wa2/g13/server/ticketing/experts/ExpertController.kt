package it.polito.wa2.g13.server.ticketing.experts

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
}
