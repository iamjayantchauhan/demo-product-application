package org.example.demo.controller

import org.example.demo.model.Product
import org.example.demo.service.ProductService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@Controller
class ProductController(private val productService: ProductService) {

    @GetMapping("/")
    fun index(): String {
        return "index"
    }

    @GetMapping("/products/load")
    fun loadProducts(model: Model): String {
        val products = productService.getAllProducts()
        model.addAttribute("products", products)
        return "fragments/products-table :: products-table"
    }

    @PostMapping("/products/add")
    fun addProduct(
        @RequestParam title: String,
        @RequestParam price: BigDecimal,
        @RequestParam(required = false) imageUrl: String?,
        @RequestParam(required = false) description: String?,
        model: Model
    ): String {
        val product = Product(
            externalId = System.currentTimeMillis(), // Use timestamp as external ID for manual entries
            title = title,
            price = price,
            imageUrl = imageUrl?.takeIf { it.isNotBlank() },
            description = description?.takeIf { it.isNotBlank() }
        )
        
        productService.saveProduct(product)
        
        // Return updated products table
        val products = productService.getAllProducts()
        model.addAttribute("products", products)
        return "fragments/products-table :: products-table"
    }

    @GetMapping("/products/{id}/edit")
    fun editProduct(@PathVariable id: Long, model: Model): String {
        val product = productService.getProductById(id)
        model.addAttribute("product", product)
        return "fragments/edit-product :: edit-form"
    }

    @PutMapping("/products/{id}")
    fun updateProduct(
        @PathVariable id: Long,
        @RequestParam title: String,
        @RequestParam price: BigDecimal,
        @RequestParam(required = false) imageUrl: String?,
        @RequestParam(required = false) description: String?,
        model: Model
    ): String {
        val existingProduct = productService.getProductById(id)
        if (existingProduct != null) {
            val updatedProduct = existingProduct.copy(
                title = title,
                price = price,
                imageUrl = imageUrl?.takeIf { it.isNotBlank() },
                description = description?.takeIf { it.isNotBlank() }
            )
            productService.updateProduct(updatedProduct)
        }
        
        // Return updated products table
        val products = productService.getAllProducts()
        model.addAttribute("products", products)
        return "fragments/products-table :: products-table"
    }

    @DeleteMapping("/products/{id}")
    fun deleteProduct(@PathVariable id: Long, model: Model): String {
        productService.deleteProduct(id)
        
        // Return updated products table
        val products = productService.getAllProducts()
        model.addAttribute("products", products)
        return "fragments/products-table :: products-table"
    }
}