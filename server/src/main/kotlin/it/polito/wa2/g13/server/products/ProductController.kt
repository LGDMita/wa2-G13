package it.polito.wa2.g13.server.products

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class ProductController(
    private val productService: ProductService
){
    @GetMapping("/API/products/")
    fun getAll(): List<ProductDTO>{
        println("product - getAll")
        return productService.getAll()
    }

    @GetMapping("/API/products/{ean}")
    fun getProduct(@PathVariable ean: String): ProductDTO? {
        println("product - getProduct ean=${ean}")
        return productService.getProduct(ean) ?: throw ProductNotFoundException()
    }
}