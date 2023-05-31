package it.polito.wa2.g13.server.products

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g13.server.jwtAuth.AuthController
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
@Observed
@Slf4j
@Validated
class ProductController(
    private val productService: ProductService
){

    private val log = LoggerFactory.getLogger(AuthController::class.java)

    @GetMapping("/API/products/")
    fun getAll(): List<ProductDTO>{
        log.info("All products listed")
        return productService.getAll()
    }

    @GetMapping("/API/products/{ean}")
    fun getProduct(@PathVariable @NotNull @NotBlank @Size(min=13, max=13) ean: String): ProductDTO? {
        log.info("Product searched with ean: {}", ean)
        return productService.getProduct(ean) ?: throw ProductNotFoundException()
    }
}