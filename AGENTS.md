# Agent Notes

When reporting information to me, be extremely concise and sacrifice grammar for the sake of concision

## Project Overview
Single-module Android app (Gradle + Kotlin) that acts as a companion alarm for Abbott FreeStyle Libre CGM users. Monitors notifications from the official FreeStyle Libre app and triggers full-screen alarms with sound/vibration.

## Architecture
- **Package:** `dev.theolm.freestyle_libre_alarm`
- **Layers:** `data/` → `domain/` → `presentation/`
- **DI:** Manual via `AppModule.kt` (no Hilt/Dagger)
- **UI:** Jetpack Compose + Material 3 + Navigation Compose
- **Design reference:** `@DESIGN.md` for app UI design decisions and conventions
- **DB:** Room (KSP codegen) with 2 entities (`AlarmEventEntity`, `NotificationLogEntity`), version 2, `fallbackToDestructiveMigration(true)`
- **Prefs:** Jetpack DataStore
- **Logging:** `AppLogger` (Kermit wrapper, tag `FreeStyleLibreAlarm`, min severity Debug)
- **Updates:** In-app updater checks GitHub releases, downloads APK, installs via intent (`REQUEST_INSTALL_PACKAGES` permission)

## Key Entrypoints
- `MainActivity.kt` — Compose UI root with 3 screens (Monitoring, History, Settings)
- `LibreNotificationListenerService` — `NotificationListenerService` that watches for `com.freestylelibre.app` notifications
- `AlarmForegroundService` — Sticky foreground service (`specialUse` type) to keep monitoring alive
- `AlarmActivity` — Full-screen alarm UI launched over lock screen
- `AlarmManager` — Handles sound, vibration, wake lock, and notification
- `BootReceiver` — Restarts foreground service on boot
- `AlarmDismissReceiver` — Broadcast receiver that stops alarm and triggers 10-min snooze (used from notification action)
- In-app updater (`UpdateRepositoryImpl`, `UpdateViewModel`) — Checks GitHub releases, downloads APK, installs via intent

## Gotchas
- `LibreNotificationListenerService` uses a `contains("freestylelibre")` prefix match (case-insensitive) to catch all regional Libre app variants instead of a hardcoded package name.
- `AlarmManager.stopAlarm()` must be called to release the wake lock and stop ringtone; missing this causes resource leaks.
- The app uses `enableEdgeToEdge()` in `MainActivity`.
- Room schema export is disabled (`exportSchema = false`).
- `enableEdgeToEdge()` is used in both `MainActivity` and `AlarmActivity`.
- Snooze system: dismiss sets `snoozeEndTime` in DataStore; listener service checks before triggering.
- All FreeStyle Libre notifications are logged to DB regardless of alarm state.
- `AlarmDismissReceiver` uses `GlobalScope` (marked with `@OptIn(DelicateCoroutinesApi::class)`).
- New permissions: `INTERNET`, `REQUEST_INSTALL_PACKAGES`.

## Agent skills

### Issue tracker

Issues are tracked in GitHub Issues (`theolm/FreeStyle-Libre-Alarm`). See `docs/agents/issue-tracker.md`.

### Triage labels

Default canonical labels (`needs-triage`, `needs-info`, `ready-for-agent`, `ready-for-human`, `wontfix`). See `docs/agents/triage-labels.md`.

### Domain docs

Single-context layout (`CONTEXT.md` + `docs/adr/` at repo root). See `docs/agents/domain.md`.

## Agent Constraints

### Git Operations Require Explicit Permission

**NEVER** execute any git command that modifies the repository without explicit user permission. This includes but is not limited to:
- `git commit`
- `git push`
- `git tag`
- `git commit --amend`
- `git rebase`
- `git merge`
- `git cherry-pick`
- `git revert`
- `git reset`
- `git checkout` (when switching branches)
- Any other operation that modifies the working tree, index, or refs

**Always ask** before running git commands. The user must explicitly approve each git operation.
