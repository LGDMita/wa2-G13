package it.polito.wa2.g13.server.jwtAuth

import org.springframework.stereotype.Service

@Service
interface AuthService {
    fun login(loginDTO: LoginDTO): JwtResponse?

    fun register(registerDTO: RegisterDTO): String?

    fun createExpert(registerDTO: RegisterDTO): String?

    fun updateUser(id: String,oldRegisterDTO: RegisterDTO,registerDTO: RegisterDTO): Boolean

    fun deleteUser(id: String)
}