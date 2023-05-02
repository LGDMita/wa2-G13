package it.polito.wa2.g13.server.ticketing.experts

import org.springframework.stereotype.Service
import org.springframework.validation.BindingResult

@Service
interface ExpertService {
    fun getExperts() : List<ExpertDTO>
}