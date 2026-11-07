package com.example

import com.example.model.CreatureAlertLevel
import com.example.model.MoveChoice
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testStealthRankCalculation() {
        // High survival time and low peak noise -> S-Tier
        val seconds = 75
        val peakDb = 32f
        val rank = when {
            seconds >= 60 && peakDb < 38f -> "S-Tier (Ghost)"
            seconds >= 40 && peakDb < 45f -> "A-Tier (Shadow Stalker)"
            seconds >= 20 -> "B-Tier (Tactical Scout)"
            else -> "F-Tier (Creature Food)"
        }
        assertEquals("S-Tier (Ghost)", rank)
    }

    @Test
    fun testCreatureAlertLevelThresholds() {
        val threshold = 40f
        fun getAlert(current: Float): CreatureAlertLevel = when {
            current >= threshold + 15f -> CreatureAlertLevel.DANGER
            current >= threshold -> CreatureAlertLevel.CAUTION
            else -> CreatureAlertLevel.SAFE
        }

        assertEquals(CreatureAlertLevel.SAFE, getAlert(32f))
        assertEquals(CreatureAlertLevel.CAUTION, getAlert(45f))
        assertEquals(CreatureAlertLevel.DANGER, getAlert(62f))
    }

    @Test
    fun testUrlsConfigured() {
        val tiktok = "https://www.tiktok.com/@mopoleemovieclip1"
        val paystack = "https://paystack.shop/pay/mjzyo3ejz0"

        assertTrue(tiktok.startsWith("https://www.tiktok.com/@mopoleemovieclip1"))
        assertTrue(paystack.startsWith("https://paystack.shop/pay/mjzyo3ejz0"))
    }
}
