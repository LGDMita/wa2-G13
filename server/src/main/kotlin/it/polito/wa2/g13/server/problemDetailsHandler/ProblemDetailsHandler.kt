package it.polito.wa2.g13.server.problemDetailsHandler

import it.polito.wa2.g13.server.products.DuplicateProductException
import it.polito.wa2.g13.server.products.ProductNotFoundException
import it.polito.wa2.g13.server.profiles.DuplicateProfileException
import it.polito.wa2.g13.server.profiles.InvalidArgumentsException
import it.polito.wa2.g13.server.profiles.ProfileNotFoundException
import it.polito.wa2.g13.server.ticketing.experts.DuplicateExpertException
import it.polito.wa2.g13.server.ticketing.experts.ExpertInvalidArgumentsException
import it.polito.wa2.g13.server.ticketing.experts.ExpertNotFoundException
import it.polito.wa2.g13.server.ticketing.experts.ExpertsOfSelectedSectorNotFoundException
import it.polito.wa2.g13.server.ticketing.sectors.SectorInvalidArgumentException
import it.polito.wa2.g13.server.ticketing.sectors.SectorsNotFoundException
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

    @ExceptionHandler(ExpertInvalidArgumentsException::class)
    fun handleDuplicateProfile(e: ExpertInvalidArgumentsException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.message!!)

    @ExceptionHandler(DuplicateExpertException::class)
    fun handleDuplicateProduct(e: DuplicateExpertException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.CONFLICT, e.message!!)

    @ExceptionHandler(ExpertNotFoundException::class)
    fun handleProductNotFound(e: ExpertNotFoundException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(ExpertsOfSelectedSectorNotFoundException::class)
    fun handleProductNotFound(e: ExpertsOfSelectedSectorNotFoundException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(SectorsNotFoundException::class)
    fun handleProductNotFound(e: SectorsNotFoundException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(SectorInvalidArgumentException::class)
    fun handleDuplicateProfile(e: SectorInvalidArgumentException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.message!!)
}