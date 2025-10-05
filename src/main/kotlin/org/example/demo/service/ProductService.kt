package org.example.demo.service

import org.example.demo.model.Product
import org.example.demo.repository.ProductRepository
import org.springframework.stereotype.Service

@Service
class ProductService(private val productRepository: ProductRepository) {

    fun saveProduct(product: Product): Product {
        return productRepository.save(product)
    }

    fun getAllProducts(): List<Product> {
        return productRepository.findAll()
    }

    fun searchProducts(query: String): List<Product> {
        return productRepository.searchByTitle(query)
    }

    fun getProductById(id: Long): Product? {
        return productRepository.findById(id)
    }

    fun updateProduct(product: Product): Product? {
        return productRepository.update(product)
    }

    fun deleteProduct(id: Long): Boolean {
        return productRepository.deleteById(id)
    }
}