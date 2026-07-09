# UI Layer (`ui/`)

All user interface code built with Jetpack Compose and Material 3. Built by Camel Creatives.

## Structure

```
ui/
├── MainViewModel.kt          # App-level state (lock status, engine init)
├── navigation/
│   └── NavGraph.kt           # Navigation routes and graph definition
├── screens/                  # One sub-directory per screen
│   ├── about/
│   ├── backup/
│   ├── blocked/
│   ├── chat/
│   ├── contacts/
│   ├── home/
│   ├── lock/
│   ├── newmessage/
│   ├── onboarding/
│   ├── search/
│   └── settings/
└── theme/
    ├── Color.kt              # Color palette
    ├── Theme.kt              # Material 3 theme (light + dark)
    └── Type.kt               # Typography scale
```

## Architecture Per Screen

Each screen follows this pattern:

```
Screen.kt          ← @Composable UI (stateless, receives callbacks)
ViewModel.kt       ← State holder (StateFlow<UiState>, action handlers)
```

- **Screens are stateless composables** — they receive state from their ViewModel and emit events via lambda callbacks
- **ViewModels use `StateFlow`** for reactive state management
- **Navigation is centralized** in `NavGraph.kt` using Navigation Compose

## Key UI Patterns

| Pattern | Usage |
|---------|-------|
| `AnimatedContent` | Onboarding step transitions |
| `LazyColumn` | All scrollable lists (threads, contacts, messages, search results) |
| `FilterChip` | Category filter row on Home screen |
| `Scaffold` | All screens with TopAppBar + content area |
| `FloatingActionButton` | New message + New contact |
| `combinedClickable` | Long-press for multi-select in thread list |
| `MessagingStyle` | Chat message bubbles with left/right alignment |
| `DropdownMenu` | Overflow menus (thread actions, chat actions) |
| `AlertDialog` | Delete confirmations |
| `Switch` | Toggle settings (notifications, lock, theme) |
| `TopAppBar` | All screens with back navigation + actions |

## Navigation Routes

| Route | Screen | Arguments |
|-------|--------|-----------|
| `onboarding` | Onboarding flow | None |
| `home` | Main conversation list | None |
| `chat/{threadId}/{address}/{name}` | Chat thread | Long, String, String |
| `new_message` | New message composer | None |
| `contacts` | Contact list | None |
| `contact_detail/{id}/{name}/{phone}` | Contact details | Long, String, String |
| `search` | Global search | None |
| `settings` | Settings menu | None |
| `settings/*` | Settings sub-screens | None |
| `lock_screen` | App lock | None |

## State Management

- `MainViewModel` holds `isLocked: StateFlow<Boolean>` for app lock
- Each screen has its own ViewModel scoped to the NavBackStackEntry
- ViewModels use `viewModelScope` for coroutine management
- Settings are observed via `AppSettings.preferences: Flow<AppPreferences>`
