# Database Layer (`data/db/`)

Room database for Messeji-specific metadata. This sits **on top of** the Android system SMS provider — it does not duplicate message content.

## Database: `AppDatabase.kt`

Single Room database version 1 with 5 entities. Uses KSP for annotation processing.

## Entities

### `MessageMeta`
Links a system SMS message (`messageId` FK to `_id` in Telephony provider) to a category and tracks whether the category was manually set by the user.

| Column | Type | Description |
|--------|------|-------------|
| `messageId` | Long (PK) | Matches `_id` from `content://sms` |
| `category` | String | "person", "money_otp", or "promo" |
| `isManualOverride` | Boolean | True if user manually recategorized |

### `ThreadMeta`
Stores per-conversation settings layered on top of the system thread.

| Column | Type | Description |
|--------|------|-------------|
| `threadId` | Long (PK) | Matches `thread_id` from Telephony |
| `isPinned` | Boolean | Pinned to top of conversation list |
| `isMuted` | Boolean | Suppress notifications |
| `isArchived` | Boolean | Hidden from main view |
| `defaultSimSlot` | Int? | Preferred SIM for this thread |

### `SenderRule`
Stores categorization rules — both system defaults and user overrides.

| Column | Type | Description |
|--------|------|-------------|
| `senderPattern` | String (PK) | Phone number or shortcode pattern |
| `forcedCategory` | String | "person", "money_otp", or "promo" |
| `createdBy` | String | "system_default" or "user_override" |

### `BlockedNumber`
List of numbers the user has blocked.

| Column | Type | Description |
|--------|------|-------------|
| `number` | String (PK) | Phone number to block |
| `blockedAt` | Long | Timestamp of block action |

### `BackupRecord`
Tracks backup history.

| Column | Type | Description |
|--------|------|-------------|
| `backupId` | Long (auto, PK) | Auto-generated ID |
| `createdAt` | Long | Backup timestamp |
| `sizeBytes` | Long | Backup file size |
| `location` | String | "local" or "drive" |
| `encrypted` | Boolean | Whether AES-256 encrypted |

## DAOs

Each entity has a corresponding DAO with `suspend` functions for writes and `Flow` return types for reactive observation.

### Notable patterns:
- `ThreadMetaDao`: Uses `suspend` get + `Flow` observe variants for both one-shot and reactive access
- `SenderRuleDao`: Filters by `createdBy` to distinguish user overrides from system defaults
- `BlockedNumberDao`: Returns `Int` count from `isBlocked()` for simple existence checks
