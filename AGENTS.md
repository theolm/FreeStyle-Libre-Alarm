# Agent Notes

## Project Overview
Single-module Android app (Gradle + Kotlin) that acts as a companion alarm for Abbott FreeStyle Libre CGM users. Monitors notifications from the official FreeStyle Libre app and triggers full-screen alarms with sound/vibration.

## Architecture
- **Package:** `dev.theolm.freestyle_libre_alarm`
- **Layers:** `data/` → `domain/` → `presentation/`
- **DI:** Manual via `AppModule.kt` (no Hilt/Dagger)
- **UI:** Jetpack Compose + Material 3 + Navigation Compose
- **Design reference:** `@DESIGN.md` for app UI design decisions and conventions
- **DB:** Room (KSP codegen) with single entity `AlarmEventEntity`
- **Prefs:** Jetpack DataStore

## Key Entrypoints
- `MainActivity.kt` — Compose UI root with 3 screens (Monitoring, History, Settings)
- `LibreNotificationListenerService` — `NotificationListenerService` that watches for `com.freestylelibre.app` notifications
- `AlarmForegroundService` — Sticky foreground service (`specialUse` type) to keep monitoring alive
- `AlarmActivity` — Full-screen alarm UI launched over lock screen
- `AlarmManager` — Handles sound, vibration, wake lock, and notification
- `BootReceiver` — Restarts foreground service on boot

## Critical Behavior
- **Any** notification from the FreeStyle Libre app triggers the alarm. The app does **not** parse notification content to distinguish low/high glucose vs. informational messages.
- Alarm forces `STREAM_ALARM` to max volume, acquires a wake lock, and launches a full-screen intent.
- UI strings are in **Portuguese** (e.g., "Alerta de Glicose", "Desligar Alarme").

## Build & Development
- **Min SDK:** 24 | **Target/Compile SDK:** 36
- **Java compatibility:** 11
- Standard Gradle wrapper: `./gradlew assembleDebug` / `./gradlew assembleRelease`
- KSP is required for Room; run builds before expecting generated code in IDE.
- No custom lint, typecheck, or formatter tasks configured.

## Permissions (Require Runtime/Settings Grants)
- `BIND_NOTIFICATION_LISTENER_SERVICE` — User must manually enable in system settings. Cannot be granted programmatically.
- `POST_NOTIFICATIONS` — Runtime permission on Android 13+.
- After reboot, notification listener access may need to be re-enabled manually for security reasons (despite `BootReceiver`).

## Testing
- Only example tests exist (`ExampleUnitTest`, `ExampleInstrumentedTest`).
- No actual test coverage for alarm logic, notification parsing, or DB operations.
- Instrumented tests require an Android device/emulator.

## Gotchas
- `LibreConstants.FREESTYLE_LIBRE_PACKAGE` defaults to `com.freestylelibre.app`. Settings allow overriding this for regional app variants.
- `AlarmManager.stopAlarm()` must be called to release the wake lock and stop ringtone; missing this causes resource leaks.
- The app uses `enableEdgeToEdge()` in `MainActivity`.
- Room schema export is disabled (`exportSchema = false`).

## Agent skills

### Issue tracker

Issues are tracked in GitHub Issues (`theolm/FreeStyle-Libre-Alarm`). See `docs/agents/issue-tracker.md`.

### Triage labels

Default canonical labels (`needs-triage`, `needs-info`, `ready-for-agent`, `ready-for-human`, `wontfix`). See `docs/agents/triage-labels.md`.

### Domain docs

Single-context layout (`CONTEXT.md` + `docs/adr/` at repo root). See `docs/agents/domain.md`.
