# SMS Manager (`data/sms/`)

## `SmsManager.kt`

Wraps Android's `Telephony.Sms` ContentResolver and `SmsManager` APIs. This is the only class that directly interacts with the system SMS provider. Built by Camel Creatives.

### Key Responsibilities

| Operation | Method | Description |
|-----------|--------|-------------|
| **Read threads** | `getAllThreads()` | Queries `Telephony.Sms.Inbox.CONTENT_URI`, groups by `thread_id`, returns `List<SmsThread>` |
| **Read messages** | `getThreadMessages(threadId)` | Queries `Telephony.Sms.CONTENT_URI` filtered by `thread_id`, ordered by date ASC |
| **Send SMS** | `sendSms(address, body, subId)` | Calls `SmsManager.sendTextMessage()`, supports per-subscription (SIM) sending |
| **Send long SMS** | `sendMultipartSms(address, body, subId)` | Uses `divideMessage()` + `sendMultipartTextMessage()` for messages > 160 chars |
| **Mark read** | `markMessageRead(id)` / `markThreadRead(threadId)` | Updates the `read` column in Telephony provider |
| **Delete** | `deleteMessage(id)` / `deleteThread(threadId)` | Deletes from Telephony provider |

### Data Models

#### `SmsThread`
Represents a conversation thread with snippet preview.

```kotlin
data class SmsThread(
    val threadId: Long,      // System thread ID
    val address: String,     // Sender/recipient phone number
    val contactName: String?, // Resolved contact name (populated by repository)
    val snippet: String?,    // Last message body preview
    val date: Long,          // Last message timestamp
    val messageCount: Int,   // Total messages in thread
    val read: Boolean,       // All messages read?
    val hasAttachment: Boolean // Contains MMS?
)
```

#### `SmsMessage`
Represents a single SMS.

```kotlin
data class SmsMessage(
    val id: Long,            // System _id
    val threadId: Long,
    val address: String,
    val contactName: String?,
    val body: String?,
    val date: Long,
    val dateSent: Long,
    val type: Int,           // 1=inbox, 2=sent, 3=draft, 4=outbox, 5=failed, 6=queued
    val read: Boolean,
    val status: Int,         // Telephony TextBasedSmsColumns status codes
    val subscriptionId: Int  // SIM slot (populated where available)
)
```

### Dual-SIM Support
Sending uses `SmsManager.getSmsManagerForSubscriptionId(subscriptionId)` when a SIM slot is specified. The `subscriptionId` is stored in `ThreadMeta.defaultSimSlot` and defaults to the user's `AppSettings.defaultSimSlot`.

### Thread Query Optimization
The `getAllThreads()` method uses a single cursor over `Telephony.Sms.Inbox` with the `"simple": "true"` query parameter to avoid loading full message content for the thread list preview. Messages per thread are counted and deduplicated in-memory.
