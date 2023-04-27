package it.polito.wa2.g13.server.ticketing.tickets

class TicketNotFoundException : RuntimeException("No ticket found for the current ticket_id")