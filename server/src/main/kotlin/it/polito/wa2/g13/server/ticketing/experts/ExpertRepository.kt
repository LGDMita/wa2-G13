package it.polito.wa2.g13.server.ticketing.experts

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ExpertRepository : JpaRepository<Expert, String> {
}