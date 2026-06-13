package dev.theolm.freestyle_libre_alarm.domain.usecase

import dev.theolm.freestyle_libre_alarm.domain.model.AppSettings

class GlucoseThresholdEvaluator {

    operator fun invoke(
        value: Int?,
        type: GlucoseType,
        settings: AppSettings
    ): Boolean {
        if (!settings.useCustomThresholds || value == null) {
            return fallbackToDirectionToggle(type, settings)
        }

        return when (type) {
            GlucoseType.LOW ->
                settings.isLowGlucoseEnabled && value <= settings.lowThresholdMgDl
            GlucoseType.HIGH ->
                settings.isHighGlucoseEnabled && value >= settings.highThresholdMgDl
        }
    }

    private fun fallbackToDirectionToggle(
        type: GlucoseType,
        settings: AppSettings
    ): Boolean = when (type) {
        GlucoseType.LOW -> settings.isLowGlucoseEnabled
        GlucoseType.HIGH -> settings.isHighGlucoseEnabled
    }

    enum class GlucoseType {
        LOW,
        HIGH
    }
}
