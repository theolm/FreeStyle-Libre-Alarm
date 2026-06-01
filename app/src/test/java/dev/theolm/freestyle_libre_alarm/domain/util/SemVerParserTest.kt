package dev.theolm.freestyle_libre_alarm.domain.util

import org.junit.Test
import org.junit.Assert.*

class SemVerParserTest {

    @Test
    fun `parse valid version with v prefix`() {
        val result = SemVerParser.parse("v1.2.3")
        assertNotNull(result)
        assertEquals(SemVersion(1, 2, 3), result)
    }

    @Test
    fun `parse valid version without v prefix`() {
        val result = SemVerParser.parse("1.2.3")
        assertNotNull(result)
        assertEquals(SemVersion(1, 2, 3), result)
    }

    @Test
    fun `parse invalid version returns null`() {
        assertNull(SemVerParser.parse("1.2"))
        assertNull(SemVerParser.parse("1.2.3.4"))
        assertNull(SemVerParser.parse("abc"))
        assertNull(SemVerParser.parse(""))
        assertNull(SemVerParser.parse("v1.2"))
    }

    @Test
    fun `parse version with whitespace`() {
        val result = SemVerParser.parse("  v1.2.3  ")
        assertNotNull(result)
        assertEquals(SemVersion(1, 2, 3), result)
    }

    @Test
    fun `isNewer when candidate is newer`() {
        assertTrue(SemVerParser.isNewer("v0.0.2", "v0.0.3"))
        assertTrue(SemVerParser.isNewer("v0.0.2", "v0.1.0"))
        assertTrue(SemVerParser.isNewer("v0.0.2", "v1.0.0"))
        assertTrue(SemVerParser.isNewer("1.2.3", "1.2.4"))
    }

    @Test
    fun `isNewer when candidate is older`() {
        assertFalse(SemVerParser.isNewer("v0.0.3", "v0.0.2"))
        assertFalse(SemVerParser.isNewer("v1.0.0", "v0.9.9"))
        assertFalse(SemVerParser.isNewer("1.2.3", "1.2.2"))
    }

    @Test
    fun `isNewer when versions are equal`() {
        assertFalse(SemVerParser.isNewer("v0.0.2", "v0.0.2"))
        assertFalse(SemVerParser.isNewer("1.2.3", "1.2.3"))
    }

    @Test
    fun `compareTo with different components`() {
        assertTrue(SemVersion(1, 0, 0) > SemVersion(0, 9, 9))
        assertTrue(SemVersion(0, 2, 0) > SemVersion(0, 1, 9))
        assertTrue(SemVersion(0, 0, 3) > SemVersion(0, 0, 2))
        assertEquals(0, SemVersion(1, 2, 3).compareTo(SemVersion(1, 2, 3)))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `isNewer throws for invalid current version`() {
        SemVerParser.isNewer("invalid", "v1.0.0")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `isNewer throws for invalid candidate version`() {
        SemVerParser.isNewer("v1.0.0", "invalid")
    }
}
