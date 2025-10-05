package org.example.demo.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.example.demo.model.Product
import org.example.demo.service.ProductService
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Controller
class ProductController(private val productService: ProductService) {

    @GetMapping("/")
    fun index(): String {
        return "index"
    }

    @GetMapping("/search")
    fun searchPage(): String {
        return "search"
    }

    @GetMapping("/products/search")
    fun searchProducts(@RequestParam(required = false, defaultValue = "") query: String, model: Model): String {
        val products = if (query.isBlank()) {
            productService.getAllProducts()
        } else {
            productService.searchProducts(query)
        }
        model.addAttribute("products", products)
        model.addAttribute("searchQuery", query)
        return "fragments/search-results :: search-results"
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

    @GetMapping("/products/{id}/edit-page")
    fun editProductPage(@PathVariable id: Long, model: Model): String {
        val product = productService.getProductById(id)
        if (product == null) {
            return "redirect:/"
        }
        model.addAttribute("product", product)
        return "edit-product-page"
    }

    @PutMapping("/products/{id}")
    @PostMapping("/products/{id}")
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

    @GetMapping("/products/export/csv")
    fun exportProductsAsCsv(): ResponseEntity<String> {
        val products = productService.getAllProducts()
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
        
        val csvContent = buildString {
            // CSV Header
            appendLine("ID,External ID,Title,Price,Image URL,Description")
            
            // CSV Data
            products.forEach { product ->
                val description = product.description?.replace("\"", "\"\"")?.replace("\n", " ") ?: ""
                val imageUrl = product.imageUrl ?: ""
                appendLine("${product.id},${product.externalId},\"${product.title}\",${product.price},\"$imageUrl\",\"$description\"")
            }
        }
        
        val headers = HttpHeaders().apply {
            contentType = MediaType.parseMediaType("text/csv")
            setContentDispositionFormData("attachment", "products_$timestamp.csv")
        }
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(csvContent)
    }

    @GetMapping("/products/export/json")
    fun exportProductsAsJson(objectMapper: ObjectMapper): ResponseEntity<String> {
        val products = productService.getAllProducts()
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
        
        val exportData = mapOf(
            "exportedAt" to LocalDateTime.now().toString(),
            "totalProducts" to products.size,
            "products" to products
        )
        
        val jsonContent = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exportData)
        
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            setContentDispositionFormData("attachment", "products_$timestamp.json")
        }
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(jsonContent)
    }
}