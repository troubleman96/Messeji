# Domain Layer (`domain/`)

Contains pure Kotlin data classes used across the app. These are the canonical representations of core business objects.

## Models

### `ContactInfo`
Represents a device contact with phone number.

```kotlin
data class ContactInfo(
    val id: Long,           // System contact _ID
    val name: String,       // Display name
    val phoneNumber: String, // Phone number string
    val photoUri: String?   // Optional contact photo URI
)
```

### `ThreadInfo`
Represents a conversation thread with all metadata needed for display. Created by `MessageRepository.loadAllThreads()`.

```kotlin
data class ThreadInfo(
    val threadId: Long,         // System thread ID
    val address: String,        // Phone number
    val contactName: String,    // Resolved name or number
    val contactPhotoUri: String?,
    val snippet: String?,       // Last message preview
    val date: Long,             // Last activity timestamp
    val messageCount: Int,
    val read: Boolean,
    val category: String,       // "person", "money_otp", "promo"
    val isPinned: Boolean,
    val isMuted: Boolean,
    val isArchived: Boolean,
    val hasAttachment: Boolean
)
```

### Design Principles

- **No framework dependencies** — These are pure Kotlin classes without Android annotations, making them testable and portable.
- **Used as UI state carriers** — ViewModels map these to UI state data classes. The domain objects are the "source of truth" representation.
- **Immutability** — All fields are `val`. State changes produce new copies via `copy()`.
