# DEACX Widget — Phase 4: Production Ready

Private, single-user Android widget project. Kotlin + XML, no Jetpack Compose.

## Stack
- MVVM (`ui` → `domain` → `data`)
- Hilt for dependency injection
- DataStore (Preferences) for local storage — no cloud, no accounts
- AppWidgetProvider + WorkManager-scheduled RemoteViews rendering, resize-aware
- Dynamic color: text + app UI (API 31+) via resource qualifiers and
  `DynamicColors.applyToActivitiesIfAvailable`; glass panel stays neutral

## Structure
```
app/src/main/java/com/deacx/widget/
├── DeacxApplication.kt   Hilt entry point + dynamic color activation
├── di/                   DataStoreModule, RepositoryModule
├── domain/               WidgetPreferences model, PreferencesRepository interface
├── data/
│   ├── local/            deacxDataStore delegate + safe (IOException-caught) read path
│   └── repository/       PreferencesRepositoryImpl
├── ui/main/              MainActivity, MainViewModel — config screen (text field, live
│                         preview, Save/Apply, Reset to Default)
└── widget/
    ├── DeacxWidgetProvider.kt   AppWidgetProvider — lifecycle, resize, error handling
    ├── render/                 DeacxWidgetRenderer — size-aware RemoteViews content
    ├── greeting/               GreetingPeriod — time-bucket + boundary logic (unit tested)
    └── schedule/               WidgetUpdateScheduler + WidgetUpdateWorker

app/src/test/java/com/deacx/widget/widget/greeting/
└── GreetingPeriodTest.kt  Boundary math coverage — the highest-risk pure logic
```

## Phase 4 notes
- **Resizing**: below ~100dp height, the widget drops to greeting-only; date and
  custom text return once there's room. Every render path (scheduled, placement,
  Save, resize) goes through the same size-aware `pushUpdate`.
- **Accessibility**: screen title marked as a heading; the config screen's live
  preview is hidden from TalkBack (decorative, duplicates the text field); Material
  components handle touch targets and labels by default.
- **RTL**: `android:supportsRtl="true"` plus layouts already built with
  start/vertical-only spacing — no left/right-specific attributes to fix.
- **Error handling**: DataStore reads fall back to defaults on `IOException` instead
  of crashing; widget update paths log-and-continue; the worker retries transient
  failures and gives up on unexpected ones; Save failures surface a Snackbar.
- **Testing**: unit tests cover `GreetingPeriod`'s boundary math (the trickiest pure
  logic — day rollover, exact-boundary edge cases). Broader instrumented/UI testing
  (on-device widget rendering, Activity UI tests) is a separate, larger effort not
  attempted here — ask if you want that built out.

## Intentionally NOT built yet
- Weather, calendar, AI features — later phases
- Advanced/widget-content animations — the original brief already scopes this as its
  own concern (placement, greeting change, config change, unlock)
- Launcher icon — left as system default until branding is requested; no image
  generation tool is available in this environment either way

## Setup

**This repo doesn't use the Gradle Wrapper** — install Gradle 8.11.1
directly instead:

- macOS/Linux: `sdk install gradle 8.11.1` (SDKMAN) or `brew install gradle`
- Windows: `choco install gradle`, or download from gradle.org and add it to PATH

Then, from the project root:
```
gradle assembleDebug
```

Opening the project in Android Studio still works too — Studio will offer
to generate a Gradle Wrapper on sync. That's independent of this setup;
fine to accept if you'd rather use `./gradlew` locally, just keep the
version consistent with what CI uses (8.11.1) if you do.

CI (`.github/workflows/android-ci.yml`) provisions Gradle 8.11.1 directly
via `gradle/actions/setup-gradle`, so no wrapper files need to be
committed for the workflow to run.

**Not compiled or run** — no Android SDK or network access in this sandbox, so
none of this (including the CI workflow itself) has actually been executed.
Toolchain versions (AGP 8.11.0, Gradle 8.11.1, Kotlin 2.0.21, Hilt 2.57.1,
WorkManager 2.11.2, JUnit 4.13.2) were checked as current at time of writing —
trust Android Studio's sync/upgrade prompts and a real CI run if anything's
moved since.

`applicationId` / package: `com.deacx.widget` — rename via Android
Studio's refactor tool if you'd prefer something else.
