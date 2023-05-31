package it.polito.wa2.g13.server.problemDetailsHandler

import it.polito.wa2.g13.server.jwtAuth.DuplicateEmailException
import it.polito.wa2.g13.server.jwtAuth.DuplicateUsernameException
import it.polito.wa2.g13.server.jwtAuth.InvalidCredentialArgumentsException
import it.polito.wa2.g13.server.jwtAuth.UserNotFoundException
import it.polito.wa2.g13.server.products.DuplicateProductException
import it.polito.wa2.g13.server.products.ProductNotFoundException
import it.polito.wa2.g13.server.profiles.DuplicateProfileException
import it.polito.wa2.g13.server.profiles.InvalidArgumentsException
import it.polito.wa2.g13.server.profiles.ProfileNotFoundException
import it.polito.wa2.g13.server.ticketing.experts.DuplicateExpertException
import it.polito.wa2.g13.server.ticketing.experts.ExpertInvalidArgumentsException
import it.polito.wa2.g13.server.ticketing.experts.ExpertNotFoundException
import it.polito.wa2.g13.server.ticketing.tickets.InvalidTicketArgumentsException
import it.polito.wa2.g13.server.ticketing.tickets.InvalidTicketException
import it.polito.wa2.g13.server.ticketing.tickets.StateChangeNotAllowedException
import it.polito.wa2.g13.server.ticketing.tickets.TicketNotFoundException
import it.polito.wa2.g13.server.ticketing.experts.ExpertsOfSelectedSectorNotFoundException
import it.polito.wa2.g13.server.ticketing.sectors.*
import it.polito.wa2.g13.server.warranty.WarrantyNotBoughtException
import jakarta.validation.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class ProblemDetailsHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(ProductNotFoundException::class)
    fun handleProductNotFound(e: ProductNotFoundException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(DuplicateProductException::class)
    fun handleDuplicateProduct(e: DuplicateProductException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.CONFLICT, e.message!!)

    @ExceptionHandler(ProfileNotFoundException::class)
    fun handleProfileNotFound(e: ProfileNotFoundException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(DuplicateProfileException::class)
    fun handleDuplicateProfile(e: DuplicateProfileException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.CONFLICT, e.message!!)

    @ExceptionHandler(InvalidArgumentsException::class)
    fun handleDuplicateProfile(e: InvalidArgumentsException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.message!!)

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleInvalidConstrain(e: ConstraintViolationException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.message!!)

    @ExceptionHandler(ExpertInvalidArgumentsException::class)
    fun handleExpertInvalidArguments(e: ExpertInvalidArgumentsException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.message!!)

    @ExceptionHandler(DuplicateExpertException::class)
    fun handleDuplicateExpert(e: DuplicateExpertException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.CONFLICT, e.message!!)

    @ExceptionHandler(ExpertNotFoundException::class)
    fun handleExpertNotFound(e: ExpertNotFoundException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(ExpertsOfSelectedSectorNotFoundException::class)
    fun handleExpertsOfSelectedSectorFound(e: ExpertsOfSelectedSectorNotFoundException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(SectorsNotFoundException::class)
    fun handleSectorsNotFound(e: SectorsNotFoundException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(SectorInvalidArgumentException::class)
    fun handleSectorInvalidArgument(e: SectorInvalidArgumentException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.message!!)

    @ExceptionHandler(ExpertSectorsNotFoundException::class)
    fun handleExpertSectorsNotFound(e: ExpertSectorsNotFoundException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(SectorNotFoundException::class)
    fun handleSectorNotFound(e: SectorNotFoundException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(ExpertSectorNotFoundException::class)
    fun handleExpertSectorNotFound(e: ExpertSectorNotFoundException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(TicketNotFoundException::class)
    fun handleTicketNotFound(e: TicketNotFoundException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(InvalidTicketArgumentsException::class)
    fun handleInvalidTicketArgumentsException(e: InvalidTicketArgumentsException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.message!!)

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleInvalidTicketArgumentsException(e: DataIntegrityViolationException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.CONFLICT, e.message!!)

    @ExceptionHandler(StateChangeNotAllowedException::class)
    fun handleInvalidTicketArgumentsException(e: StateChangeNotAllowedException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.CONFLICT, e.message!!)

    @ExceptionHandler(InvalidTicketException::class)
    fun handleInvalidTicket(e: InvalidTicketException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.message!!)

    @ExceptionHandler(InvalidCredentialArgumentsException::class)
    fun handleInvalidTicket(e: InvalidCredentialArgumentsException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.UNAUTHORIZED, e.message!!)

    @ExceptionHandler(WarrantyNotBoughtException::class)
    fun handleWarrantyNotBought(e: WarrantyNotBoughtException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.message!!)
    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(e: UserNotFoundException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(DuplicateEmailException::class)
    fun handleInvalidTicketArgumentsException(e: DuplicateEmailException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.CONFLICT, e.message!!)

    @ExceptionHandler(DuplicateUsernameException::class)
    fun handleInvalidTicketArgumentsException(e: DuplicateUsernameException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.CONFLICT, e.message!!)
}