package it.polito.wa2.g13.server.jwtAuth

class InvalidCredentialArgumentsException : RuntimeException("The username and/or password you have provided is incorrect!")