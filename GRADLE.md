# Gradle Build System

Project-level build configuration for the Messeji Android app. Built by Camel Creatives.

## Files

### `build.gradle.kts` (root)
Root project Gradle config. Declares plugins without applying them:

| Plugin | Version | Purpose |
|--------|---------|---------|
| `com.android.application` | 8.2.2 | Android build tools |
| `org.jetbrains.kotlin.android` | 1.9.22 | Kotlin compiler |
| `com.google.dagger.hilt.android` | 2.50 | Dependency injection |
| `com.google.devtools.ksp` | 1.9.22-1.0.17 | Symbol processing (Room, Hilt) |

### `settings.gradle.kts`
Plugin repository configuration and module declaration. Uses `google()`, `mavenCentral()`, and `gradlePluginPortal()` repositories.

### `gradle.properties`
Build performance and compatibility flags:

```properties
org.gradle.jvmargs=-Xmx2048m          # 2GB heap for Gradle daemon
android.useAndroidX=true              # AndroidX compatibility
kotlin.code.style=official            # Kotlin coding style
android.nonTransitiveRClass=true      # Non-transitive R classes
```

### `app/build.gradle.kts`
Module-level build config with all dependencies.

## Build Variants

| Variant | Minification | Shrink Resources | Use Case |
|---------|-------------|-----------------|----------|
| `debug` | No | No | Development, testing |
| `release` | ProGuard + R8 | Yes | Production distribution |

## Dependencies Summary

| Category | Libraries |
|----------|-----------|
| **UI** | Compose BOM 2024.01, Material 3, Icons Extended, Foundation |
| **Lifecycle** | ViewModel Compose, Runtime Compose, LiveData |
| **DI** | Hilt 2.50, Hilt Navigation Compose, KSP |
| **Database** | Room 2.6.1, Room KTX, KSP compiler |
| **Async** | Kotlin Coroutines 1.7.3 |
| **Settings** | DataStore Preferences 1.0.0 |
| **Background** | WorkManager 2.9.0 |
| **Security** | Biometric 1.2.0, Security Crypto 1.1.0 |
| **Testing** | JUnit 4.13.2, Espresso 3.5.1, Compose UI Test |

## Building

```bash
# Debug APK (~8-10MB)
./gradlew assembleDebug

# Release APK (~4-6MB with ProGuard)
./gradlew assembleRelease

# Run on device
./gradlew installDebug

# Run tests
./gradlew testDebugUnitTest
```

## ProGuard (`app/proguard-rules.pro`)
- Keeps Hilt/Dagger generated classes
- Keeps Room entities and database classes
- Preserves Kotlin Coroutines dispatcher
- Excludes Compose warnings
