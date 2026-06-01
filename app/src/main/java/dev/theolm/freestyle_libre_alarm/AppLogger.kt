package dev.theolm.freestyle_libre_alarm

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity

object AppLogger {
    val log = Logger.withTag("FreeStyleLibreAlarm")
    
    init {
        Logger.setMinSeverity(Severity.Debug)
    }
}
