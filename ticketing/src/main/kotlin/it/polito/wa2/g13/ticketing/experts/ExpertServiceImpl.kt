package it.polito.wa2.g13.ticketing.experts

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.validation.BindingResult

@Service
class ExpertServiceImpl(
    private val expertRepository: ExpertRepository
) : ExpertService {

}