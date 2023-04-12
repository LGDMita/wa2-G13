package it.polito.wa2.g13.server.products

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
@Validated
class ProductController(
    private val productService: ProductService
){
    @GetMapping("/API/products/")
    fun getAll(): List<ProductDTO>{
        return productService.getAll()
    }

    @GetMapping("/API/products/{ean}")
    fun getProduct(@PathVariable @NotNull @NotBlank @Size(min=13, max=13) ean: String): ProductDTO? {
        return productService.getProduct(ean) ?: throw ProductNotFoundException()
    }
}