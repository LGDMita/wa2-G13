package it.polito.wa2.g13.server.products

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class ProductController(
    private val productService: ProductService
){
    @CrossOrigin(origins = ["http://localhost:3000"])
    @GetMapping("/products/")
    fun getAll(): List<ProductDTO>{
        return productService.getAll()
    }

    @CrossOrigin(origins = ["http://localhost:3000"])
    @GetMapping("/products/{ean}")
    fun getProduct(@PathVariable ean: String): ProductDTO? {
        return productService.getProduct(ean) ?: throw ProductNotFoundException()
    }
}