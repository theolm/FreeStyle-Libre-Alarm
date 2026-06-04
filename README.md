# FreeStyle Libre Alarm

Your FreeStyle Libre notifications are too quiet. This app turns them into a **real alarm** — full volume, full screen, impossible to miss.

Built for my friend **Matthias**, who kept missing low-glucose alerts while sleeping. A quiet notification at 3 AM can be dangerous. This app fixes that.

## The Problem

The official FreeStyle Libre app sends notifications when your glucose is too low or too high. But notifications are easy to miss — especially when you're asleep. A low-glucose event during the night can go unnoticed for hours.

## The Solution

**FreeStyle Libre Alarm** listens for every notification from the FreeStyle Libre app and immediately triggers a **full-screen alarm** with:

- Maximum volume sound
- Strong vibration
- Screen wake-up, even through the lock screen

It turns a silent notification into an alarm you **cannot sleep through**.

## Features

- **Full-screen alarm** — Launches over the lock screen with loud sound and vibration
- **Notification monitoring** — Automatically detects alerts from the FreeStyle Libre app
- **Alarm history** — Keeps a log of all triggered alarms with timestamps
- **Monitoring toggle** — Easily enable or disable monitoring from the main screen
- **Configurable volume** — Adjust alarm volume from 0–100%
- **Custom package name** — Supports different regional variants of the FreeStyle Libre app
- **Foreground service** — Persistent background monitoring with minimal battery impact
- **Boot handling** — Automatically resumes after device restart

## Requirements

- Android 7.0+ (API 24)
- Abbott FreeStyle Libre app installed and configured
- Notification access permission granted to this app

## Setup

1. Install the APK on your Android device.
2. Open the app and grant **Notification Access** permission when prompted.
3. Ensure the Abbott FreeStyle Libre app is installed and actively monitoring your glucose.
4. Toggle monitoring **ON** in the app.

> **Note:** After rebooting your device, you may need to manually re-grant notification access for security reasons.

## How It Works

The app uses Android's `NotificationListenerService` to monitor all notifications. When it detects a notification from the FreeStyle Libre app, it immediately triggers a high-priority alarm activity that:

- Turns on the screen
- Plays the default alarm ringtone at the configured volume
- Vibrates with a repeating pattern
- Displays a full-screen intent even if the phone is locked

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose (Material 3)
- **Architecture:** MVVM with StateFlow
- **Database:** Room (SQLite)
- **Preferences:** Jetpack DataStore
- **Min SDK:** 24 (Android 7.0)
- **Target/Compile SDK:** 36 (Android 16)

## Building from Source

```bash
# Clone the repository
git clone <repo-url>
cd FreeStyle-Libre-Alarm

# Build the debug APK
./gradlew assembleDebug
```

The generated APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

## Important Notes

- This app triggers an alarm on **any notification** posted by the FreeStyle Libre app. It does not distinguish between low glucose, high glucose, or informational notifications.
- This is a companion app and does not replace the official FreeStyle Libre application. You must have the official app installed and running.
- The alarm uses the audio stream volume, so ensure your device volume is not muted.

## Permissions

This app requires the following permissions:

- `BIND_NOTIFICATION_LISTENER_SERVICE` — Required to monitor FreeStyle Libre notifications
- `WAKE_LOCK` / `VIBRATE` — Required to wake the device and vibrate during alarms
- `MODIFY_AUDIO_SETTINGS` — Required to adjust alarm volume
- `POST_NOTIFICATIONS` — Required to show alarm notifications
- `FOREGROUND_SERVICE` — Required for persistent background monitoring
- `RECEIVE_BOOT_COMPLETED` — Required to resume monitoring after reboot

## Disclaimer

This is an unofficial companion app and is **not** affiliated with or endorsed by Abbott. Always rely on the official FreeStyle Libre app and your healthcare provider for medical decisions. This app is provided as-is without warranty of any kind.
