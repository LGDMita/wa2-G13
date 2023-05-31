package it.polito.wa2.g13.server.ticketing.sectors

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class SectorDTO(
    val sectorId: Long?,
    @field:Size(min = 1, max = 31, message = "Name MUST be a NON empty string of max 31 chars")
    @field:NotBlank(message = "Name can NOT be blank")
    val name: String,
) {
    override fun toString(): String {
        return "SectorId=$sectorId&Name=$name"
    }
}

fun Sector.toDTO(): SectorDTO {
    return SectorDTO(getId(), name)
}