# Data Layer (`data/`)

The data layer handles all persistence, SMS system interaction, and business logic for message categorization. It is organized into four sub-packages.

## Structure

```
data/
├── AppSettings.kt              # User preferences via DataStore
├── categorization/
│   └── CategoryEngine.kt       # On-device message categorization
├── db/
│   ├── AppDatabase.kt          # Room database definition
│   ├── dao/                    # Data Access Objects
│   ├── entity/                 # Room entity classes
├── repository/
│   └── MessageRepository.kt    # Single source of truth
└── sms/
    └── SmsManager.kt           # Telephony ContentResolver wrapper
```

## Key Design Decisions

### SMS Content Lives in System Provider
Messeji does **not** copy SMS data into its own database. SMS/MMS content stays in the Android `Telephony` provider (`content://sms`, `content://mms`). Room is only used for **metadata**:
- Category tags per message (`message_meta`)
- Pin/mute/archive state per thread (`thread_meta`)
- User override rules for sender categorization (`sender_rules`)
- Blocked numbers (`blocked_numbers`)
- Backup records (`backup_records`)

This avoids duplication, keeps the app lightweight, and ensures compatibility with other SMS tools.

### Settings via DataStore
`AppSettings.kt` wraps Jetpack DataStore Preferences, providing a type-safe `Flow<AppPreferences>` that the UI observes. All settings (theme, lock, notifications, SIM preference, backup) are stored here.

### Repository Pattern
`MessageRepository` is the single source of truth. ViewModels never access the DAO or SMS provider directly. This centralizes logic for:
- Loading threads with metadata (pin, mute, archive, category)
- Sending messages with SIM selection
- Categorization with user override support
- Block/unblock management
