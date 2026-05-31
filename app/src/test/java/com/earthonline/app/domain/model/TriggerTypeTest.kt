package com.earthonline.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TriggerTypeTest {

    @Test
    fun `fromValue should parse LOCATION_CHECKIN_COUNT`() {
        assertEquals(TriggerType.LOCATION_CHECKIN_COUNT, TriggerType.fromValue("LOCATION_CHECKIN_COUNT"))
    }

    @Test
    fun `fromValue should parse MANUAL_CONFIRM`() {
        assertEquals(TriggerType.MANUAL_CONFIRM, TriggerType.fromValue("MANUAL_CONFIRM"))
    }

    @Test
    fun `fromValue should parse AUTO_TRACK`() {
        assertEquals(TriggerType.AUTO_TRACK, TriggerType.fromValue("AUTO_TRACK"))
    }

    @Test
    fun `fromValue should return null for unknown type`() {
        assertNull(TriggerType.fromValue("INVALID_TYPE"))
        assertNull(TriggerType.fromValue(""))
    }

    @Test
    fun `fromValue should be case sensitive`() {
        assertNull(TriggerType.fromValue("location_checkin_count"))
    }
}
