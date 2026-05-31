package com.earthonline.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class RarityTest {

    @Test
    fun `fromPoints should return COMMON for 0-49 points`() {
        assertEquals(Rarity.COMMON, Rarity.fromPoints(0))
        assertEquals(Rarity.COMMON, Rarity.fromPoints(25))
        assertEquals(Rarity.COMMON, Rarity.fromPoints(49))
    }

    @Test
    fun `fromPoints should return RARE for 50-199 points`() {
        assertEquals(Rarity.RARE, Rarity.fromPoints(50))
        assertEquals(Rarity.RARE, Rarity.fromPoints(100))
        assertEquals(Rarity.RARE, Rarity.fromPoints(199))
    }

    @Test
    fun `fromPoints should return EPIC for 200-999 points`() {
        assertEquals(Rarity.EPIC, Rarity.fromPoints(200))
        assertEquals(Rarity.EPIC, Rarity.fromPoints(500))
        assertEquals(Rarity.EPIC, Rarity.fromPoints(999))
    }

    @Test
    fun `fromPoints should return LEGENDARY for 1000+ points`() {
        assertEquals(Rarity.LEGENDARY, Rarity.fromPoints(1000))
        assertEquals(Rarity.LEGENDARY, Rarity.fromPoints(5000))
    }

    @Test
    fun `fromPoints should return highest qualifying rarity`() {
        // 50 points qualifies for RARE, not COMMON
        assertEquals(Rarity.RARE, Rarity.fromPoints(50))
        // 1000 points qualifies for LEGENDARY, not EPIC
        assertEquals(Rarity.LEGENDARY, Rarity.fromPoints(1000))
    }

    @Test
    fun `labelResId should not be zero for any rarity`() {
        Rarity.entries.forEach { rarity ->
            assert(rarity.labelResId != 0) { "${rarity.name} has zero labelResId" }
        }
    }
}
