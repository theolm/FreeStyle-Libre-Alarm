package dev.theolm.freestyle_libre_alarm.domain.usecase

import dev.theolm.freestyle_libre_alarm.domain.model.AppSettings
import dev.theolm.freestyle_libre_alarm.domain.usecase.GlucoseThresholdEvaluator.GlucoseType
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GlucoseThresholdEvaluatorTest {

    private val evaluator = GlucoseThresholdEvaluator()
    private val baseSettings = AppSettings(
        isLowGlucoseEnabled = true,
        isHighGlucoseEnabled = true,
        useCustomThresholds = true,
        lowThresholdMgDl = 70,
        highThresholdMgDl = 180
    )

    @Test
    fun `low value at threshold triggers alarm`() {
        assertTrue(evaluator(70, GlucoseType.LOW, baseSettings))
    }

    @Test
    fun `low value below threshold triggers alarm`() {
        assertTrue(evaluator(65, GlucoseType.LOW, baseSettings))
    }

    @Test
    fun `low value above threshold does not trigger alarm`() {
        assertFalse(evaluator(75, GlucoseType.LOW, baseSettings))
    }

    @Test
    fun `high value at threshold triggers alarm`() {
        assertTrue(evaluator(180, GlucoseType.HIGH, baseSettings))
    }

    @Test
    fun `high value above threshold triggers alarm`() {
        assertTrue(evaluator(200, GlucoseType.HIGH, baseSettings))
    }

    @Test
    fun `high value below threshold does not trigger alarm`() {
        assertFalse(evaluator(170, GlucoseType.HIGH, baseSettings))
    }

    @Test
    fun `low direction disabled does not trigger alarm`() {
        val settings = baseSettings.copy(isLowGlucoseEnabled = false)
        assertFalse(evaluator(60, GlucoseType.LOW, settings))
    }

    @Test
    fun `high direction disabled does not trigger alarm`() {
        val settings = baseSettings.copy(isHighGlucoseEnabled = false)
        assertFalse(evaluator(200, GlucoseType.HIGH, settings))
    }

    @Test
    fun `custom thresholds disabled falls back to direction toggle`() {
        val settings = baseSettings.copy(useCustomThresholds = false)
        assertTrue(evaluator(200, GlucoseType.HIGH, settings))
        assertTrue(evaluator(60, GlucoseType.LOW, settings))
    }

    @Test
    fun `custom thresholds disabled and direction disabled does not trigger`() {
        val settings = baseSettings.copy(
            useCustomThresholds = false,
            isLowGlucoseEnabled = false
        )
        assertFalse(evaluator(60, GlucoseType.LOW, settings))
    }

    @Test
    fun `unparseable value falls back to direction toggle`() {
        assertTrue(evaluator(null, GlucoseType.HIGH, baseSettings))
        assertTrue(evaluator(null, GlucoseType.LOW, baseSettings))
    }

    @Test
    fun `unparseable value with disabled direction does not trigger`() {
        val settings = baseSettings.copy(isHighGlucoseEnabled = false)
        assertFalse(evaluator(null, GlucoseType.HIGH, settings))
    }
}
