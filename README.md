# 🍦 Cone Counter

A quick, cute Android app for tracking how many ice cream cones your kids eat on a trip.

## Features

- Add multiple kids, each with a pick-your-own emoji avatar, color, and daily scoop goal
- Log a scoop in two taps: pick a kid, pick a flavor
- Home screen shows each kid's progress today plus a family-wide daily goal
- Log tab: full scoop history, swipe to delete
- Stats tab: weekly bar chart and a fun leaderboard
- **Home screen shortcuts** — pin a "+1 scoop" icon for each kid straight to the home screen (long-press the app icon or use the home icon next to a kid in the Kids tab) for a one-tap log, no need to open the app
- 100% local storage (Room + DataStore) — no accounts, no network, works great with airplane mode on a cruise

## Tech

- Kotlin + Jetpack Compose (Material 3)
- Room for scoop/kid data, DataStore Preferences for trip settings
- `ShortcutManagerCompat` for dynamic + pinned per-kid shortcuts

## Building

Open in Android Studio (Koala+) and run, or from the CLI:

```
./gradlew assembleDebug
```

The debug APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

A GitHub Actions workflow (`.github/workflows/build-debug-apk.yml`) builds the debug APK on every push/PR and uploads it as a workflow artifact — grab it from the Actions run summary to side-load onto a phone for testing.
