package it.polito.wa2.g13.server.ticketing.tickets

import java.util.*

data class TicketPostDTO(
    var ticketId: Long,
    var profileId: String,
    var ean: String
)