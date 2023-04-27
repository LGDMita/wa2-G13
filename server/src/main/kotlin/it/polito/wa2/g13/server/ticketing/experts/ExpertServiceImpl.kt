package it.polito.wa2.g13.server.ticketing.experts

import org.springframework.stereotype.Service

@Service
class ExpertServiceImpl(
    private val expertRepository: ExpertRepository
) : ExpertService