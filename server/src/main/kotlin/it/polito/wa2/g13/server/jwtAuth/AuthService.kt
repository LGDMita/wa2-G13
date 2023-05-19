package it.polito.wa2.g13.server.jwtAuth

import org.springframework.stereotype.Service

@Service
interface AuthService {
    fun login(loginDTO: LoginDTO): JwtResponse
}