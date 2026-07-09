# Messeji — Core Application (`com.sendafrica.messeji`)

This is the root package for the Messeji Android application. All source code lives under this namespace.

## Package Overview

| Package | Purpose |
|---------|---------|
| `data/` | Data layer: Room database, SMS provider wrapper, categorization engine, repository |
| `di/` | Hilt dependency injection module |
| `domain/` | Domain models used across the app |
| `notification/` | Notification channel creation and message notification display |
| `service/` | Background services: SMS receiver, boot receiver, foreground service |
| `ui/` | All UI code: theme, navigation, screens, ViewModels |
| `util/` | Utility classes: contact resolution, time formatting |

## Entry Points

- **`MessejiApp.kt`** — `Application` subclass annotated with `@HiltAndroidApp`. Configures WorkManager with HiltWorkerFactory.
- **`MainActivity.kt`** — Single-activity architecture. Hosts Jetpack Compose UI via `setContent()`. Reads theme preference from `AppSettings` and applies `MessejiTheme`. Uses `installSplashScreen()` for branded splash.
- **`MainViewModel.kt`** — Global ViewModel for app-level state (lock status). Initializes the categorization engine on startup.

## Architecture Pattern

MVVM with unidirectional data flow:
- **UI** (Compose screens) observes `StateFlow` from ViewModels
- **ViewModels** call `MessageRepository` methods
- **Repository** coordinates between Room DB, SMS ContentResolver, and CategoryEngine
- Data flows down, events flow up
