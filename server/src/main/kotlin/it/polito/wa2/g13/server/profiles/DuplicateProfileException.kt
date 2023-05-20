package it.polito.wa2.g13.server.profiles

class DuplicateProfileException : RuntimeException("User with the same username and/or email already exists!")