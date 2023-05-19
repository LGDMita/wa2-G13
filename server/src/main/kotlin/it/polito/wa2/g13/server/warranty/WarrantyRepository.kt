package it.polito.wa2.g13.server.warranty

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WarrantyRepository : JpaRepository<Warranty,Long>