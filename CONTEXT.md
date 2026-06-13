# Domain glossary

## Update check
A background operation that compares the currently installed app version against the latest published release on GitHub.

## Update notification
A local operating-system notification that tells the user a newer app version is available and invites them to install it.

## Foreground service
The persistent Android service that keeps the app's monitoring alive. It is the natural host for periodic, background work such as an update check.

## Dismissed version
A release version the user has previously declined to install. The app uses this to avoid repeatedly showing update dialogs or notifications for the same release.
