package com.namabiz.agrim.core.model

import com.namabiz.agrim.core.database.SupplierEntity
import com.namabiz.agrim.data.repository.toDomainModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ModelMappersTest {

    @Test
    fun testUserIsPremiumGetter() {
        val freeUser = User(id = 1, name = "Free Buyer", email = "free@test.com", subscriptionTier = "free")
        assertFalse("Free user should not be premium", freeUser.isPremium)

        val premiumUser = User(id = 2, name = "Premium Buyer", email = "pro@test.com", subscriptionTier = "premium")
        assertTrue("Premium user should be premium", premiumUser.isPremium)

        val businessProUser = User(id = 3, name = "Pro Buyer", email = "bus@test.com", subscriptionTier = "Business Pro")
        assertTrue("Business Pro user should be premium", businessProUser.isPremium)
    }

    @Test
    fun testSupplierEntityToDomainMapping() {
        val entity = SupplierEntity(
            id = 10,
            name = "Test Agro Ltd",
            slug = "test-agro-ltd",
            logo = "https://example.com/logo.jpg",
            tagline = "Exporter of High Grade Spices",
            description = "Detailed supplier description",
            country = "Indonesia",
            city = "Surabaya",
            industryCategory = "Spices & Herbs",
            isVerified = true,
            about = "About us...",
            website = "https://agrim.com",
            isSaved = true,
            moq = "1 MT",
            capacity = "100 MT/month",
            leadTime = "7 days",
            tagsJson = "[\"HACCP\",\"ISO 22000\",\"Halal MUI\"]"
        )

        val domain = entity.toDomainModel()

        assertEquals(10, domain.id)
        assertEquals("Test Agro Ltd", domain.name)
        assertEquals("test-agro-ltd", domain.slug)
        assertEquals("Indonesia", domain.country)
        assertTrue(domain.isVerified)
        assertTrue(domain.isSaved)
        assertEquals(3, domain.tags.size)
        assertTrue(domain.tags.contains("HACCP"))
        assertTrue(domain.tags.contains("ISO 22000"))
        assertTrue(domain.tags.contains("Halal MUI"))
    }

    @Test
    fun testSupplierEntityFallbackWhenTagsJsonInvalid() {
        val entityWithCorruptJson = SupplierEntity(
            id = 11,
            name = "Corrupt JSON Ltd",
            slug = "corrupt-json-ltd",
            logo = null,
            tagline = null,
            description = null,
            country = "Vietnam",
            city = null,
            industryCategory = "Coconut",
            isVerified = false,
            about = "About...",
            website = null,
            isSaved = false,
            moq = null,
            capacity = null,
            leadTime = null,
            tagsJson = "INVALID_NOT_JSON"
        )

        val domain = entityWithCorruptJson.toDomainModel()

        assertEquals(11, domain.id)
        // Should fall back gracefully to default tags without throwing
        assertFalse(domain.tags.isEmpty())
    }
}
