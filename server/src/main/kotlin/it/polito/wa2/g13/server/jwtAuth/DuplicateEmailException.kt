package it.polito.wa2.g13.server.jwtAuth

class DuplicateEmailException : RuntimeException("A user with the same email already exists!")