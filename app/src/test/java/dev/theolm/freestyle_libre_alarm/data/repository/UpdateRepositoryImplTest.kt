package dev.theolm.freestyle_libre_alarm.data.repository

import dev.theolm.freestyle_libre_alarm.domain.model.UpdateInfo
import dev.theolm.freestyle_libre_alarm.domain.util.SemVerParser
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class UpdateRepositoryImplTest {

    @Test
    fun `semVerParser parse valid version`() {
        val result = SemVerParser.parse("v1.2.3")
        assertNotNull(result)
        assertEquals(1, result?.major)
        assertEquals(2, result?.minor)
        assertEquals(3, result?.patch)
    }

    @Test
    fun `semVerParser isNewer correctly identifies newer version`() {
        assertTrue(SemVerParser.isNewer("v0.0.2", "v0.0.3"))
        assertTrue(SemVerParser.isNewer("v0.0.2", "v0.1.0"))
        assertTrue(SemVerParser.isNewer("v0.0.2", "v1.0.0"))
    }

    @Test
    fun `semVerParser isNewer returns false for older or equal`() {
        assertFalse(SemVerParser.isNewer("v0.0.3", "v0.0.2"))
        assertFalse(SemVerParser.isNewer("v0.0.2", "v0.0.2"))
    }
}
