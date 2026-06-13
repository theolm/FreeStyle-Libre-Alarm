package dev.theolm.freestyle_libre_alarm.domain.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GlucoseValueParserTest {

    @Test
    fun `parse value with mg slash dL unit`() {
        assertEquals(120, GlucoseValueParser.parse("120mg/dL"))
    }

    @Test
    fun `parse value with space before unit`() {
        assertEquals(120, GlucoseValueParser.parse("120 mg/dL"))
    }

    @Test
    fun `parse value with trend arrow`() {
        assertEquals(85, GlucoseValueParser.parse("85 mg/dL →"))
    }

    @Test
    fun `parse plain numeric value`() {
        assertEquals(70, GlucoseValueParser.parse("70"))
    }

    @Test
    fun `parse returns first value when multiple values present`() {
        assertEquals(95, GlucoseValueParser.parse("95 mg/dL · 14:32"))
    }

    @Test
    fun `parse returns null for missing text`() {
        assertNull(GlucoseValueParser.parse(null))
    }

    @Test
    fun `parse returns null for blank text`() {
        assertNull(GlucoseValueParser.parse("   "))
    }

    @Test
    fun `parse returns null when no numeric value`() {
        assertNull(GlucoseValueParser.parse("HIGH"))
        assertNull(GlucoseValueParser.parse("Low glucose"))
    }
}
