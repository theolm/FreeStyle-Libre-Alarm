package dev.theolm.freestyle_libre_alarm.domain.util

object GlucoseValueParser {
    private val GLUCOSE_VALUE_REGEX =
        Regex("""(\d+)(?:\s*mg/dL)?""", RegexOption.IGNORE_CASE)

    @Suppress("ReturnCount")
    fun parse(text: String?): Int? {
        if (text.isNullOrBlank()) return null

        val match = GLUCOSE_VALUE_REGEX.find(text.trim()) ?: return null
        return match.groupValues[1].toIntOrNull()
    }
}
