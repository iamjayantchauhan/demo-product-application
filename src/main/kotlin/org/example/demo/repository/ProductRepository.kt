package org.example.demo.repository

import org.example.demo.model.Product
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.sql.ResultSet

@Repository
class ProductRepository(private val jdbcClient: JdbcClient) {

    fun save(product: Product): Product {
        val keyHolder = GeneratedKeyHolder()
        
        val sql = """
            INSERT INTO products (external_id, title, price, image_url, description, variants)
            VALUES (?, ?, ?, ?, ?, ?::jsonb)
            ON CONFLICT (external_id) 
            DO UPDATE SET 
                title = EXCLUDED.title,
                price = EXCLUDED.price,
                image_url = EXCLUDED.image_url,
                description = EXCLUDED.description,
                variants = EXCLUDED.variants,
                updated_at = CURRENT_TIMESTAMP
            RETURNING id
        """.trimIndent()

        jdbcClient.sql(sql)
            .params(
                product.externalId,
                product.title,
                product.price,
                product.imageUrl,
                product.description,
                product.variants
            )
            .update(keyHolder)

        val generatedId = keyHolder.key?.toLong() ?: product.id
        return product.copy(id = generatedId)
    }

    fun findAll(): List<Product> {
        val sql = "SELECT * FROM products ORDER BY title"
        return jdbcClient.sql(sql)
            .query { rs, _ -> mapRowToProduct(rs) }
            .list()
    }

    fun findById(id: Long): Product? {
        val sql = "SELECT * FROM products WHERE id = ?"
        return jdbcClient.sql(sql)
            .param(id)
            .query { rs, _ -> mapRowToProduct(rs) }
            .optional()
            .orElse(null)
    }

    fun update(product: Product): Product? {
        val sql = """
            UPDATE products 
            SET title = ?, price = ?, image_url = ?, description = ?, updated_at = CURRENT_TIMESTAMP
            WHERE id = ?
        """.trimIndent()

        val rowsAffected = jdbcClient.sql(sql)
            .params(
                product.title,
                product.price,
                product.imageUrl,
                product.description,
                product.id
            )
            .update()

        return if (rowsAffected > 0) product else null
    }

    fun deleteById(id: Long): Boolean {
        val sql = "DELETE FROM products WHERE id = ?"
        val rowsAffected = jdbcClient.sql(sql)
            .param(id)
            .update()
        return rowsAffected > 0
    }

    private fun mapRowToProduct(rs: ResultSet): Product {
        return Product(
            id = rs.getLong("id"),
            externalId = rs.getLong("external_id"),
            title = rs.getString("title"),
            price = rs.getBigDecimal("price"),
            imageUrl = rs.getString("image_url"),
            description = rs.getString("description"),
            variants = rs.getString("variants")
        )
    }
}