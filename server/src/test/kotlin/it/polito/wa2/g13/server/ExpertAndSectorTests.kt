package it.polito.wa2.g13.server

import it.polito.wa2.g13.server.ticketing.experts.Expert
import it.polito.wa2.g13.server.ticketing.experts.ExpertDTO
import it.polito.wa2.g13.server.ticketing.experts.ExpertRepository
import it.polito.wa2.g13.server.ticketing.experts.toDTO
import it.polito.wa2.g13.server.ticketing.sectors.Sector
import it.polito.wa2.g13.server.ticketing.sectors.SectorDTO
import it.polito.wa2.g13.server.ticketing.sectors.SectorRepository
import it.polito.wa2.g13.server.ticketing.sectors.toDTO
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.*
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.net.URI


@Testcontainers
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
class ExpertAndSectorTests {

	companion object {
		@Container
		val postgres = PostgreSQLContainer("postgres:latest")

		@JvmStatic
		@DynamicPropertySource
		fun properties(registry: DynamicPropertyRegistry) {
			registry.add("spring.datasource.url", postgres::getJdbcUrl)
			registry.add("spring.datasource.username", postgres::getUsername)
			registry.add("spring.datasource.password", postgres::getPassword)
			registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
		}
	}

	@LocalServerPort
	protected var port: Int = 0
	@Autowired
	lateinit var restTemplate: TestRestTemplate
	@Autowired
	lateinit var expertRepository: ExpertRepository
	@Autowired
	lateinit var sectorRepository: SectorRepository

	@Test
	@DirtiesContext(methodMode= DirtiesContext.MethodMode.AFTER_METHOD)
	fun expertSetSuccessTest() {
		val baseUrl = "http://localhost:$port/API/experts"
		val uri = URI(baseUrl)
		val expertDTO= ExpertDTO(expertId = 1, name = "Will", surname = "Hunting", email = "will@gmail.com")

		val headers = HttpHeaders()
		headers.set("X-COM-PERSIST", "true")

		val request = HttpEntity(expertDTO, headers)

		val result = restTemplate.postForEntity(uri, request, String::class.java)

		Assertions.assertEquals(HttpStatus.CREATED, result.statusCode)
		Assertions.assertEquals(expertDTO, expertRepository.findByIdOrNull(1)?.toDTO())
	}

	@Test
	@DirtiesContext(methodMode= DirtiesContext.MethodMode.AFTER_METHOD)
	fun expertSetConflictTest() {
		expertRepository.save(Expert(name = "William", surname = "Hunt", email = "will@gmail.com"))
		val baseUrl = "http://localhost:$port/API/experts"
		val uri = URI(baseUrl)
		val expertDTO= ExpertDTO(expertId = 1, name = "Will", surname = "Hunting", email = "will@gmail.com")

		val headers = HttpHeaders()
		headers.set("X-COM-PERSIST", "true")

		val request = HttpEntity(expertDTO, headers)

		val result = restTemplate.postForEntity(uri, request, String::class.java)

		Assertions.assertEquals(HttpStatus.CONFLICT, result.statusCode)
		Assertions.assertEquals("\"detail\":\"An expert with this email address already exists!\"",
			result.body.toString().split(",")[3])
	}

	@Test
	@DirtiesContext(methodMode= DirtiesContext.MethodMode.AFTER_METHOD)
	fun expertSetUnprocessableEntityTest() {
		val baseUrl = "http://localhost:$port/API/experts"
		val uri = URI(baseUrl)
		val expertDTO= ExpertDTO(expertId = 1, name = "Will", surname = "", email = "will@ gmail.com")

		val headers = HttpHeaders()
		headers.set("X-COM-PERSIST", "true")

		val request = HttpEntity(expertDTO, headers)

		val result = restTemplate.postForEntity(uri, request, String::class.java)

		Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result.statusCode)
		Assertions.assertEquals("\"detail\":\"The inserted input for the expert is not valid!\"",
			result.body.toString().split(",")[3])
	}

	@Test
	@DirtiesContext(methodMode= DirtiesContext.MethodMode.AFTER_METHOD)
	fun expertGetSuccess() {
		expertRepository.save(Expert(name = "William", surname = "Hunt", email = "will@gmail.com"))
		val baseUrl = "http://localhost:$port/API/experts/1"
		val uri = URI(baseUrl)

		val result = restTemplate.getForEntity(uri, String::class.java)

		Assertions.assertEquals(HttpStatus.OK, result.statusCode)
		Assertions.assertEquals("{\"expertId\":1,\"name\":\"William\",\"surname\":\"Hunt\",\"email\":\"will@gmail.com\"}",
			result.body)
	}

	@Test
	@DirtiesContext(methodMode= DirtiesContext.MethodMode.AFTER_METHOD)
	fun expertGetNotFound() {
		val baseUrl = "http://localhost:$port/API/experts/1"
		val uri = URI(baseUrl)

		val result = restTemplate.getForEntity(uri, String::class.java)

		Assertions.assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
		Assertions.assertEquals("\"detail\":\"Expert Not Found!\"",
			result.body.toString().split(",")[3])
	}

	@Test
	@DirtiesContext(methodMode= DirtiesContext.MethodMode.AFTER_METHOD)
	fun expertModifySuccessTest1() {
		expertRepository.save(Expert(name = "William", surname = "Hunt", email = "will@gmail.com"))
		val baseUrl = "http://localhost:$port/API/experts/1"
		val uri = URI(baseUrl)
		val expertDTO= ExpertDTO(expertId = 1, name = "Will", surname = "Hunting", email = "will@gmail.com")

		val headers = HttpHeaders()
		headers.set("X-COM-PERSIST", "true")

		val request = HttpEntity(expertDTO, headers)

		val result = restTemplate.exchange(uri, HttpMethod.PUT, request, String::class.java)

		Assertions.assertEquals(HttpStatus.OK, result.statusCode)
		val expert= expertRepository.findByIdOrNull(1)
		Assertions.assertEquals("Will", expert?.name)
		Assertions.assertEquals("Hunting", expert?.surname)
	}

	@Test
	@DirtiesContext(methodMode= DirtiesContext.MethodMode.AFTER_METHOD)
	fun expertModifySuccessTest2() {
		expertRepository.save(Expert(name = "William", surname = "Hunt", email = "will@gmail.com"))
		val baseUrl = "http://localhost:$port/API/experts/1"
		val uri = URI(baseUrl)
		val expertDTO= ExpertDTO(expertId = 1, name = "William", surname = "Hunt", email = "william@gmail.com")

		val headers = HttpHeaders()
		headers.set("X-COM-PERSIST", "true")

		val request = HttpEntity(expertDTO, headers)

		val result = restTemplate.exchange(uri, HttpMethod.PUT, request, String::class.java)

		Assertions.assertEquals(HttpStatus.OK, result.statusCode)
		Assertions.assertEquals("william@gmail.com", expertRepository.findByIdOrNull(1)?.email)
	}

	@Test
	@DirtiesContext(methodMode= DirtiesContext.MethodMode.AFTER_METHOD)
	fun expertModifyUnprocessableEntityTest() {
		expertRepository.save(Expert(name = "William", surname = "Hunt", email = "will@gmail.com"))
		val baseUrl = "http://localhost:$port/API/experts/1"
		val uri = URI(baseUrl)
		val expertDTO= ExpertDTO(expertId = 1, name = "William", surname = "Hunt", email = "william @gmail.com")

		val headers = HttpHeaders()
		headers.set("X-COM-PERSIST", "true")

		val request = HttpEntity(expertDTO, headers)

		val result = restTemplate.exchange(uri, HttpMethod.PUT, request, String::class.java)

		Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result.statusCode)
		Assertions.assertEquals("\"detail\":\"The inserted input for the expert is not valid!\"",
			result.body.toString().split(",")[3])
	}

	@Test
	@DirtiesContext(methodMode= DirtiesContext.MethodMode.AFTER_METHOD)
	fun expertModifyNotFoundTest() {
		expertRepository.save(Expert(name = "William", surname = "Hunt", email = "will@gmail.com"))
		val baseUrl = "http://localhost:$port/API/experts/2"
		val uri = URI(baseUrl)
		val expertDTO= ExpertDTO(expertId = 2, name = "Will", surname = "Hunt", email = "william@gmail.com")

		val headers = HttpHeaders()
		headers.set("X-COM-PERSIST", "true")

		val request = HttpEntity(expertDTO, headers)

		val result = restTemplate.exchange(uri, HttpMethod.PUT, request, String::class.java)

		Assertions.assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
		Assertions.assertEquals("\"detail\":\"Expert Not Found!\"",
			result.body.toString().split(",")[3])
	}

	@Test
	@DirtiesContext(methodMode= DirtiesContext.MethodMode.AFTER_METHOD)
	fun expertModifyConflictTest() {
		expertRepository.save(Expert(name = "William", surname = "Hunt", email = "will@gmail.com"))
		expertRepository.save(Expert(name = "Will", surname = "Hunting", email = "william@gmail.com"))
		val baseUrl = "http://localhost:$port/API/experts/2"
		val uri = URI(baseUrl)
		val expertDTO= ExpertDTO(expertId = 2, name = "Will", surname = "Hunt", email = "will@gmail.com")

		val headers = HttpHeaders()
		headers.set("X-COM-PERSIST", "true")

		val request = HttpEntity(expertDTO, headers)

		val result = restTemplate.exchange(uri, HttpMethod.PUT, request, String::class.java)

		Assertions.assertEquals(HttpStatus.CONFLICT, result.statusCode)
		Assertions.assertEquals("\"detail\":\"An expert with this email address already exists!\"",
			result.body.toString().split(",")[3])
	}

	@Test
	@DirtiesContext(methodMode= DirtiesContext.MethodMode.AFTER_METHOD)
	fun expertDeleteSuccessTest() {
		expertRepository.save(Expert(name = "William", surname = "Hunt", email = "will@gmail.com"))
		val baseUrl = "http://localhost:$port/API/experts/1"
		val uri = URI(baseUrl)

		val headers = HttpHeaders()
		headers.set("X-COM-PERSIST", "true")

		val request = HttpEntity(null, headers)

		val result = restTemplate.exchange(uri, HttpMethod.DELETE, request, String::class.java)

		Assertions.assertEquals(HttpStatus.OK, result.statusCode)
		Assertions.assertEquals(null, expertRepository.findByIdOrNull(1))

	}

	@Test
	@DirtiesContext(methodMode= DirtiesContext.MethodMode.AFTER_METHOD)
	fun expertDeleteNotFoundTest() {
		val baseUrl = "http://localhost:$port/API/experts/1"
		val uri = URI(baseUrl)

		val headers = HttpHeaders()
		headers.set("X-COM-PERSIST", "true")

		val request = HttpEntity(null, headers)

		val result = restTemplate.exchange(uri, HttpMethod.DELETE, request, String::class.java)

		Assertions.assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
		Assertions.assertEquals("\"detail\":\"Expert Not Found!\"",
			result.body.toString().split(",")[3])
	}

	@Test
	@DirtiesContext(methodMode= DirtiesContext.MethodMode.AFTER_METHOD)
	fun expertsBySectorGetSuccessTest() {
		val expert1= Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
		val expert2= Expert(name = "Will", surname = "Hunting", email = "will@gmail.com")
		expertRepository.save(expert1)
		expertRepository.save(expert2)
		val sector= Sector(sectorId = 1, name = "linux")
		expert1.addSector(sector)
		expert2.addSector(sector)
		sector.addExpert(expert1)
		sector.addExpert(expert2)
		sectorRepository.save(sector)
		expertRepository.save(expert1)
		expertRepository.save(expert2)

		val baseUrl = "http://localhost:$port/API/experts/?sectorName=LINUX"
		val uri = URI(baseUrl)

		val result = restTemplate.getForEntity(uri, String::class.java)

		Assertions.assertEquals(HttpStatus.OK, result.statusCode)
		Assertions.assertEquals("[{\"expertId\":1,\"name\":\"William\",\"surname\":\"Hunt\",\"email\":\"william@gmail.com\"},{\"expertId\":2,\"name\":\"Will\",\"surname\":\"Hunting\",\"email\":\"will@gmail.com\"}]",
			result.body)
	}

	@Test
	@DirtiesContext(methodMode= DirtiesContext.MethodMode.AFTER_METHOD)
	fun expertsBySectorGetSectorsNotFoundTest() {
		val expert1= Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
		val expert2= Expert(name = "Will", surname = "Hunting", email = "will@gmail.com")
		expertRepository.save(expert1)
		expertRepository.save(expert2)

		val baseUrl = "http://localhost:$port/API/experts/?sectorName=LINUX"
		val uri = URI(baseUrl)

		val result = restTemplate.getForEntity(uri, String::class.java)

		Assertions.assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
		Assertions.assertEquals("\"detail\":\"There are currently no sectors in the DB!\"",
			result.body.toString().split(",")[3])
	}


	@Test
	@DirtiesContext(methodMode= DirtiesContext.MethodMode.AFTER_METHOD)
	fun expertsBySectorGetSectorNotFoundTest() {
		val expert1= Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
		val expert2= Expert(name = "Will", surname = "Hunting", email = "will@gmail.com")
		expertRepository.save(expert1)
		expertRepository.save(expert2)
		val sector= Sector(sectorId = 1, name = "Hardware")
		expert1.addSector(sector)
		sector.addExpert(expert1)
		sectorRepository.save(sector)
		expertRepository.save(expert1)

		val baseUrl = "http://localhost:$port/API/experts/?sectorName=LINUX"
		val uri = URI(baseUrl)

		val result = restTemplate.getForEntity(uri, String::class.java)

		Assertions.assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
		Assertions.assertEquals("\"detail\":\"The selected sector does not exist!\"",
			result.body.toString().split(",")[3])
	}

	@Test
	@DirtiesContext(methodMode= DirtiesContext.MethodMode.AFTER_METHOD)
	fun expertsBySectorGetExpertOfSelectedSectorNotFoundTest() {
		val expert1= Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
		val expert2= Expert(name = "Will", surname = "Hunting", email = "will@gmail.com")
		expertRepository.save(expert1)
		expertRepository.save(expert2)
		val sector= Sector(sectorId = 1, name = "linux")
		sectorRepository.save(sector)

		val baseUrl = "http://localhost:$port/API/experts/?sectorName=LINUX"
		val uri = URI(baseUrl)

		val result = restTemplate.getForEntity(uri, String::class.java)

		Assertions.assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
		Assertions.assertEquals("\"detail\":\"There are no experts associated with the selected sector!\"",
			result.body.toString().split(",")[3])
	}

	@Test
	@DirtiesContext(methodMode= DirtiesContext.MethodMode.AFTER_METHOD)
	fun sectorsAllGetSuccessTest() {
		val expert1= Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
		val expert2= Expert(name = "Will", surname = "Hunting", email = "will@gmail.com")
		expertRepository.save(expert1)
		expertRepository.save(expert2)
		val sector1= Sector(sectorId = 1, name = "linux")
		val sector2= Sector(sectorId = 2, name = "wi-fi")
		expert1.addSector(sector1)
		expert1.addSector(sector2)
		sector1.addExpert(expert1)
		sector2.addExpert(expert1)
		sectorRepository.save(sector1)
		sectorRepository.save(sector2)
		expertRepository.save(expert1)

		val baseUrl = "http://localhost:$port/API/experts/sectors"
		val uri = URI(baseUrl)

		val result = restTemplate.getForEntity(uri, String::class.java)

		Assertions.assertEquals(HttpStatus.OK, result.statusCode)
		Assertions.assertEquals("[{\"sectorId\":1,\"name\":\"linux\"},{\"sectorId\":2,\"name\":\"wi-fi\"}]", result.body)
	}

	@Test
	@DirtiesContext(methodMode= DirtiesContext.MethodMode.AFTER_METHOD)
	fun sectorsAllGetNotFoundTest() {
		val expert1= Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
		val expert2= Expert(name = "Will", surname = "Hunting", email = "will@gmail.com")
		expertRepository.save(expert1)
		expertRepository.save(expert2)

		val baseUrl = "http://localhost:$port/API/experts/sectors"
		val uri = URI(baseUrl)

		val result = restTemplate.getForEntity(uri, String::class.java)

		Assertions.assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
		Assertions.assertEquals("\"detail\":\"There are currently no sectors in the DB!\"",
			result.body.toString().split(",")[3])
	}

	@Test
	@DirtiesContext(methodMode= DirtiesContext.MethodMode.AFTER_METHOD)
	fun sectorsOfExpertGetSuccessTest() {
		val expert1= Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
		val expert2= Expert(name = "Will", surname = "Hunting", email = "will@gmail.com")
		expertRepository.save(expert1)
		expertRepository.save(expert2)
		val sector1= Sector(sectorId = 1, name = "linux")
		val sector2= Sector(sectorId = 2, name = "wi-fi")
		expert1.addSector(sector1)
		expert1.addSector(sector2)
		sector1.addExpert(expert1)
		sector2.addExpert(expert1)
		sectorRepository.save(sector1)
		sectorRepository.save(sector2)
		expertRepository.save(expert1)

		val baseUrl = "http://localhost:$port/API/experts/1/sectors"
		val uri = URI(baseUrl)

		val result = restTemplate.getForEntity(uri, String::class.java)

		Assertions.assertEquals(HttpStatus.OK, result.statusCode)
		Assertions.assertEquals("[{\"sectorId\":1,\"name\":\"linux\"},{\"sectorId\":2,\"name\":\"wi-fi\"}]", result.body)
	}

	@Test
	@DirtiesContext(methodMode= DirtiesContext.MethodMode.AFTER_METHOD)
	fun sectorsOfExpertGetExpertNotFoundTest() {
		val expert1= Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
		val expert2= Expert(name = "Will", surname = "Hunting", email = "will@gmail.com")
		expertRepository.save(expert1)
		expertRepository.save(expert2)
		val sector1= Sector(sectorId = 1, name = "linux")
		val sector2= Sector(sectorId = 2, name = "wi-fi")
		expert1.addSector(sector1)
		expert1.addSector(sector2)
		sector1.addExpert(expert1)
		sector2.addExpert(expert1)
		sectorRepository.save(sector1)
		sectorRepository.save(sector2)
		expertRepository.save(expert1)

		val baseUrl = "http://localhost:$port/API/experts/3/sectors"
		val uri = URI(baseUrl)

		val result = restTemplate.getForEntity(uri, String::class.java)

		Assertions.assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
		Assertions.assertEquals("\"detail\":\"Expert Not Found!\"",
			result.body.toString().split(",")[3])
	}

	@Test
	@DirtiesContext(methodMode= DirtiesContext.MethodMode.AFTER_METHOD)
	fun sectorsOfExpertGetExpertSectorsNotFoundTest() {
		val expert1= Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
		val expert2= Expert(name = "Will", surname = "Hunting", email = "will@gmail.com")
		expertRepository.save(expert1)
		expertRepository.save(expert2)
		val sector1= Sector(sectorId = 1, name = "linux")
		val sector2= Sector(sectorId = 2, name = "wi-fi")
		expert1.addSector(sector1)
		expert1.addSector(sector2)
		sector1.addExpert(expert1)
		sector2.addExpert(expert1)
		sectorRepository.save(sector1)
		sectorRepository.save(sector2)
		expertRepository.save(expert1)

		val baseUrl = "http://localhost:$port/API/experts/2/sectors"
		val uri = URI(baseUrl)

		val result = restTemplate.getForEntity(uri, String::class.java)

		Assertions.assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
		Assertions.assertEquals("\"detail\":\"There are no sectors associated with the selected expert!\"",
			result.body.toString().split(",")[3])
	}

	@Test
	@DirtiesContext(methodMode= DirtiesContext.MethodMode.AFTER_METHOD)
	fun sectorForExpertSetSuccessTest() {
		val expert1= Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
		expertRepository.save(expert1)
		val sectorDTO= SectorDTO(sectorId = 1, name = "linux")
		val baseUrl = "http://localhost:$port/API/experts/1/sectors"
		val uri = URI(baseUrl)

		val headers = HttpHeaders()
		headers.set("X-COM-PERSIST", "true")

		val request = HttpEntity(sectorDTO, headers)

		val result = restTemplate.postForEntity(uri, request, String::class.java)

		Assertions.assertEquals(HttpStatus.OK, result.statusCode)
		Assertions.assertEquals(sectorDTO, sectorRepository.findByIdOrNull(1)?.toDTO())

	}

	@Test
	@DirtiesContext(methodMode= DirtiesContext.MethodMode.AFTER_METHOD)
	fun sectorForExpertSetUnprocessableEntityTest() {
		val expert1= Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
		expertRepository.save(expert1)
		val sectorDTO= SectorDTO(sectorId = 1, name = "")
		val baseUrl = "http://localhost:$port/API/experts/1/sectors"
		val uri = URI(baseUrl)

		val headers = HttpHeaders()
		headers.set("X-COM-PERSIST", "true")

		val request = HttpEntity(sectorDTO, headers)

		val result = restTemplate.postForEntity(uri, request, String::class.java)

		Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result.statusCode)
		Assertions.assertEquals("\"detail\":\"The inserted input for the sector is not valid!\"",
			result.body.toString().split(",")[3])
	}

	@Test
	@DirtiesContext(methodMode= DirtiesContext.MethodMode.AFTER_METHOD)
	fun sectorForExpertSetExpertNotFoundTest() {
		val expert1= Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
		expertRepository.save(expert1)
		val sectorDTO= SectorDTO(sectorId = 1, name = "cellular network")
		val baseUrl = "http://localhost:$port/API/experts/2/sectors"
		val uri = URI(baseUrl)

		val headers = HttpHeaders()
		headers.set("X-COM-PERSIST", "true")

		val request = HttpEntity(sectorDTO, headers)

		val result = restTemplate.postForEntity(uri, request, String::class.java)

		Assertions.assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
		Assertions.assertEquals("\"detail\":\"Expert Not Found!\"",
			result.body.toString().split(",")[3])
	}

	@Test
	@DirtiesContext(methodMode= DirtiesContext.MethodMode.AFTER_METHOD)
	fun sectorForExpertDeleteSuccessTest() {
		val expert1= Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
		val expert2= Expert(name = "Will", surname = "Hunting", email = "will@gmail.com")
		expertRepository.save(expert1)
		expertRepository.save(expert2)
		val sector1= Sector(sectorId = 1, name = "linux")
		val sector2= Sector(sectorId = 2, name = "wi-fi")
		expert1.addSector(sector1)
		expert1.addSector(sector2)
		sector1.addExpert(expert1)
		sector2.addExpert(expert1)
		sectorRepository.save(sector1)
		sectorRepository.save(sector2)
		expertRepository.save(expert1)
		val baseUrl = "http://localhost:$port/API/experts/1/sectors/1"
		val uri = URI(baseUrl)

		val headers = HttpHeaders()
		headers.set("X-COM-PERSIST", "true")

		val request = HttpEntity(null, headers)

		val result = restTemplate.exchange(uri, HttpMethod.DELETE, request, String::class.java)

		Assertions.assertEquals(HttpStatus.OK, result.statusCode)
	}

	@Test
	@DirtiesContext(methodMode= DirtiesContext.MethodMode.AFTER_METHOD)
	fun sectorForExpertDeleteExpertNotFoundTest() {
		val expert1= Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
		val expert2= Expert(name = "Will", surname = "Hunting", email = "will@gmail.com")
		expertRepository.save(expert1)
		expertRepository.save(expert2)
		val sector1= Sector(sectorId = 1, name = "linux")
		val sector2= Sector(sectorId = 2, name = "wi-fi")
		expert1.addSector(sector1)
		expert1.addSector(sector2)
		sector1.addExpert(expert1)
		sector2.addExpert(expert1)
		sectorRepository.save(sector1)
		sectorRepository.save(sector2)
		expertRepository.save(expert1)
		val baseUrl = "http://localhost:$port/API/experts/3/sectors/1"
		val uri = URI(baseUrl)

		val headers = HttpHeaders()
		headers.set("X-COM-PERSIST", "true")

		val request = HttpEntity(null, headers)

		val result = restTemplate.exchange(uri, HttpMethod.DELETE, request, String::class.java)

		Assertions.assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
		Assertions.assertEquals("\"detail\":\"Expert Not Found!\"",
			result.body.toString().split(",")[3])
	}

	@Test
	@DirtiesContext(methodMode= DirtiesContext.MethodMode.AFTER_METHOD)
	fun sectorForExpertDeleteSectorsNotFoundTest() {
		val expert1= Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
		val expert2= Expert(name = "Will", surname = "Hunting", email = "will@gmail.com")
		expertRepository.save(expert1)
		expertRepository.save(expert2)

		val baseUrl = "http://localhost:$port/API/experts/1/sectors/1"
		val uri = URI(baseUrl)

		val headers = HttpHeaders()
		headers.set("X-COM-PERSIST", "true")

		val request = HttpEntity(null, headers)

		val result = restTemplate.exchange(uri, HttpMethod.DELETE, request, String::class.java)

		Assertions.assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
		Assertions.assertEquals("\"detail\":\"There are currently no sectors in the DB!\"",
			result.body.toString().split(",")[3])
	}

	@Test
	@DirtiesContext(methodMode= DirtiesContext.MethodMode.AFTER_METHOD)
	fun sectorForExpertDeleteSectorNotFoundTest() {
		val expert1= Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
		val expert2= Expert(name = "Will", surname = "Hunting", email = "will@gmail.com")
		expertRepository.save(expert1)
		expertRepository.save(expert2)
		val sector1= Sector(sectorId = 1, name = "linux")
		val sector2= Sector(sectorId = 2, name = "wi-fi")
		expert1.addSector(sector1)
		sectorRepository.save(sector1)
		sectorRepository.save(sector2)
		expertRepository.save(expert1)

		val baseUrl = "http://localhost:$port/API/experts/1/sectors/3"
		val uri = URI(baseUrl)

		val headers = HttpHeaders()
		headers.set("X-COM-PERSIST", "true")

		val request = HttpEntity(null, headers)

		val result = restTemplate.exchange(uri, HttpMethod.DELETE, request, String::class.java)

		Assertions.assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
		Assertions.assertEquals("\"detail\":\"The selected sector does not exist!\"",
			result.body.toString().split(",")[3])
	}

	@Test
	@DirtiesContext(methodMode= DirtiesContext.MethodMode.AFTER_METHOD)
	fun sectorForExpertDeleteExpertSectorsNotFoundTest() {
		val expert1= Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
		val expert2= Expert(name = "Will", surname = "Hunting", email = "will@gmail.com")
		expertRepository.save(expert1)
		expertRepository.save(expert2)
		val sector1= Sector(sectorId = 1, name = "linux")
		val sector2= Sector(sectorId = 2, name = "wi-fi")
		expert1.addSector(sector1)
		sectorRepository.save(sector1)
		sectorRepository.save(sector2)
		expertRepository.save(expert1)

		val baseUrl = "http://localhost:$port/API/experts/2/sectors/1"
		val uri = URI(baseUrl)

		val headers = HttpHeaders()
		headers.set("X-COM-PERSIST", "true")

		val request = HttpEntity(null, headers)

		val result = restTemplate.exchange(uri, HttpMethod.DELETE, request, String::class.java)

		Assertions.assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
		Assertions.assertEquals("\"detail\":\"There are no sectors associated with the selected expert!\"",
			result.body.toString().split(",")[3])
	}

	@Test
	@DirtiesContext(methodMode= DirtiesContext.MethodMode.AFTER_METHOD)
	fun sectorForExpertDeleteExpertSectorNotFoundTest() {
		val expert1= Expert(name = "William", surname = "Hunt", email = "william@gmail.com")
		val expert2= Expert(name = "Will", surname = "Hunting", email = "will@gmail.com")
		expertRepository.save(expert1)
		expertRepository.save(expert2)
		val sector1= Sector(sectorId = 1, name = "linux")
		val sector2= Sector(sectorId = 2, name = "wi-fi")
		expert1.addSector(sector1)
		sectorRepository.save(sector1)
		sectorRepository.save(sector2)
		expertRepository.save(expert1)

		val baseUrl = "http://localhost:$port/API/experts/1/sectors/2"
		val uri = URI(baseUrl)

		val headers = HttpHeaders()
		headers.set("X-COM-PERSIST", "true")

		val request = HttpEntity(null, headers)

		val result = restTemplate.exchange(uri, HttpMethod.DELETE, request, String::class.java)

		Assertions.assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
		Assertions.assertEquals("\"detail\":\"The selected sector is not linked with the selected expert!\"",
			result.body.toString().split(",")[3])
	}

}
