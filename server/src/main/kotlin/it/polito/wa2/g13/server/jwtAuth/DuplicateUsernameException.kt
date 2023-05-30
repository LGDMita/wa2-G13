package it.polito.wa2.g13.server.jwtAuth

class DuplicateUsernameException : RuntimeException("A user with the same username already exists!")