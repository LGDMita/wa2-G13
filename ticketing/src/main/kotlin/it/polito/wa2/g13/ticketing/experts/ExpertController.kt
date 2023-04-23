package it.polito.wa2.g13.ticketing.experts

import org.springframework.web.bind.annotation.*

@RestController
class ExpertController(
    private val expertService: ExpertService
) {

}
