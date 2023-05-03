package it.polito.wa2.g13.server.ticketing.sectors

class SectorNotFoundException : RuntimeException("The selected sector does not exist!") {
}