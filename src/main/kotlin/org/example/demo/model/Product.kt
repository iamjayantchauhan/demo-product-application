package org.example.demo.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class Product(
    val id: Long? = null,
    @JsonProperty("id") val externalId: Long,
    val title: String,
    val price: BigDecimal,
    val imageUrl: String? = null,
    val description: String? = null,
    val variants: String? = null // JSON string
)

// API response models
data class ProductResponse(
    val products: List<ApiProduct>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ApiProduct(
    val id: Long,
    val title: String,
    @JsonProperty("body_html") val bodyHtml: String?,
    val variants: List<ApiVariant>,
    val images: List<ApiImage>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ApiVariant(
    val id: Long,
    val title: String,
    val price: String,
    val available: Boolean
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ApiImage(
    val id: Long,
    val src: String,
    val alt: String?
)