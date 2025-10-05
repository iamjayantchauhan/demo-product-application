package org.example.demo.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.example.demo.model.ApiProduct
import org.example.demo.model.Product
import org.example.demo.model.ProductResponse
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import java.math.BigDecimal

@Service
class ProductFetchService(
    private val productService: ProductService,
    private val webClient: WebClient = WebClient.builder()
        .codecs { configurer ->
            configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024) // 2MB
        }
        .build()
) {
    private val logger = LoggerFactory.getLogger(ProductFetchService::class.java)
    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    @Scheduled(initialDelay = 0, fixedRate = Long.MAX_VALUE) // Run once at startup
    fun fetchProducts() {
        logger.info("Starting to fetch products from API...")
        
        try {
            val response = webClient
                .get()
                .uri("https://famme.no/products.json")
                .retrieve()
                .bodyToMono<String>()
                .block()

            if (response != null) {
                val productResponse = objectMapper.readValue<ProductResponse>(response)
                val products = productResponse.products
                    .take(50) // Limit to 50 products
                    .map { apiProduct -> convertToProduct(apiProduct) }

                products.forEach { product ->
                    try {
                        productService.saveProduct(product)
                        logger.debug("Saved product: ${product.title}")
                    } catch (e: Exception) {
                        logger.warn("Failed to save product ${product.title}: ${e.message}")
                    }
                }

                logger.info("Successfully fetched and saved ${products.size} products")
            }
        } catch (e: Exception) {
            logger.error("Failed to fetch products from API", e)
        }
    }

    private fun convertToProduct(apiProduct: ApiProduct): Product {
        val price = apiProduct.variants.firstOrNull()?.price?.toBigDecimalOrNull() ?: BigDecimal.ZERO
        val imageUrl = apiProduct.images.firstOrNull()?.src
        val description = apiProduct.bodyHtml?.take(500) // Limit description length
        val variantsJson = objectMapper.writeValueAsString(apiProduct.variants)

        return Product(
            externalId = apiProduct.id,
            title = apiProduct.title,
            price = price,
            imageUrl = imageUrl,
            description = description,
            variants = variantsJson
        )
    }
}