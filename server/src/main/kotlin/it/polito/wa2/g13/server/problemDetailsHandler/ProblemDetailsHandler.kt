package it.polito.wa2.g13.server.problemDetailsHandler

import it.polito.wa2.g13.server.products.DuplicateProductException
import it.polito.wa2.g13.server.products.ProductNotFoundException
import it.polito.wa2.g13.server.profiles.DuplicateProfileException
import it.polito.wa2.g13.server.profiles.InvalidArgumentsException
import it.polito.wa2.g13.server.profiles.ProfileNotFoundException
import jakarta.validation.ConstraintViolationException
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

    @ExceptionHandler(TicketNotFoundException::class)
    fun handleTicketNotFound(e: TicketNotFoundException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(InvalidTicketArgumentsException::class)
    fun handleInvalidTicketArgumentsException(e: InvalidTicketArgumentsException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.message!!)

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleInvalidTicketArgumentsException(e: DataIntegrityViolationException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.CONFLICT, e.message!!)

    @ExceptionHandler(ExpertNotFoundException::class)
    fun handleTicketNotFound(e: ExpertNotFoundException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(StateChangeNotAllowedException::class)
    fun handleInvalidTicketArgumentsException(e: StateChangeNotAllowedException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.CONFLICT, e.message!!)



    @ExceptionHandler(InvalidTicketException::class)
    fun handleInvalidTicket(e: InvalidTicketException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.message!!)

    @ExceptionHandler(TicketNotFoundException::class)
    fun handleInvalidTicket(e: TicketNotFoundException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.message!!)
}