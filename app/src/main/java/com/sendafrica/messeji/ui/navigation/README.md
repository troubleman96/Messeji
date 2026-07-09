# Navigation (`ui/navigation/`)

Centralized navigation graph using Jetpack Navigation Compose. Built by Camel Creatives.

## `NavGraph.kt`

Defines a `sealed class Screen` with route strings and argument helpers:

```kotlin
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Chat : Screen("chat/{threadId}/{address}/{name}") {
        fun createRoute(threadId: Long, address: String, name: String) = "chat/$threadId/$address/$name"
    }
    // ... 15+ route definitions
}
```

### Route Design
- Named parameters via `{paramName}` syntax
- `NavType` specifications for type-safe argument parsing
- Factory methods (e.g., `Screen.Chat.createRoute()`) for type-safe navigation call sites

### `MessejiNavGraph()`
The top-level composable that hosts all navigation destinations:

```kotlin
@Composable
fun MessejiNavGraph(
    navController: NavHostController,
    startDestination: String,
    isLocked: Boolean
)
```

- **Lock screen interceptor**: If `isLocked` is true and onboarding is complete and lock is enabled, the start destination becomes `Screen.Lock` instead of the normal route.
- **Onboarding routing**: Conditionally routes to Onboarding or Home based on `onboardingComplete` preference.
- **Pop-up management**: After onboarding completes, the onboarding screen is removed from the backstack via `popUpTo(inclusive = true)`.

### Navigation Flow
```
App Launch
  ├── First run → Onboarding (6 steps) → Home
  └── Returning user → Lock (if enabled) → Home
        ├── Tap thread → Chat
        ├── Tap + → New Message
        ├── Tap search → Search
        ├── Tap contacts → Contacts → Contact Detail
        └── Tap settings → Settings → Sub-screens
```
