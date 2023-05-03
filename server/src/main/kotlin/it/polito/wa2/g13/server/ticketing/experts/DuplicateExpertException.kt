package it.polito.wa2.g13.server.ticketing.experts

class DuplicateExpertException : RuntimeException("An expert with this email address already exists!")