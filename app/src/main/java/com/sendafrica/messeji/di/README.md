# Dependency Injection (`di/`)

## `AppModule.kt`

Hilt module providing singleton-scoped dependencies across the app. Built by Camel Creatives.

### Provided Dependencies

| Provider | Scope | Used By |
|----------|-------|---------|
| `ContentResolver` | Singleton | `SmsManager`, `ContactResolver` |
| `AppDatabase` | Singleton | All DAOs |
| `MessageMetaDao` | Default | `MessageRepository` |
| `ThreadMetaDao` | Default | `MessageRepository` |
| `SenderRuleDao` | Default | `MessageRepository`, `CategoryEngine` |
| `BlockedNumberDao` | Default | `MessageRepository` |
| `BackupRecordDao` | Default | `MessageRepository` |

### Module Design

- Room database is created with the standard `Room.databaseBuilder()` pattern
- Database name: `messeji_metadata.db`
- DAOs are provided via simple getter delegation from the database instance
- `ContentResolver` is sourced from the application context
- All dependencies are `@Singleton` scoped for consistency

### Why Hilt?

Hilt simplifies DI on Android by:
- Managing lifecycle-scoped containers (ViewModel, Activity, Service)
- Generating Dagger components at compile time via KSP
- Providing `@HiltViewModel` for ViewModel injection
- `@AndroidEntryPoint` for Android framework classes (Activities, Receivers, Services)
