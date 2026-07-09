# Repository (`data/repository/`)

## `MessageRepository.kt`

The single source of truth for all data operations. ViewModels call this repository — they never access Room DAOs or the SMS ContentResolver directly. Built by Camel Creatives.

### Dependencies (injected via Hilt)

| Dependency | Role |
|---|---|
| `SmsManager` | Low-level SMS read/write/delete |
| `CategoryEngine` | Message categorization logic |
| `MessageMetaDao` | Category tags |
| `ThreadMetaDao` | Pin/mute/archive/SIM preferences |
| `BlockedNumberDao` | Blocked numbers |
| `BackupRecordDao` | Backup history |
| `ContactResolver` | Contact name/photo lookup |

### Key Methods

#### Thread Operations
| Method | Description |
|--------|-------------|
| `loadAllThreads()` | Loads SMS threads, enriches with metadata (pinned/muted/archived/category), filters blocked senders, sorts pinned-first then by date |
| `deleteThread(threadId)` | Deletes from system SMS provider + cleans up Room metadata |
| `togglePinThread(threadId, pinned)` | Creates or updates `ThreadMeta` |
| `toggleMuteThread(threadId, muted)` | Same pattern |
| `archiveThread(threadId)` | Sets `isArchived = true` |

#### Message Operations
| Method | Description |
|--------|-------------|
| `getThreadMessages(threadId)` | Delegates to `SmsManager` |
| `sendMessage(address, body, simSlot)` | Sends via appropriate SIM, auto-detects multipart |
| `deleteMessage(messageId)` | Deletes + cleans up MessageMeta |
| `markThreadRead(threadId)` | Marks all messages in thread as read |

#### Categorization
| Method | Description |
|--------|-------------|
| `getThreadCategory(threadId)` | Examines last message, runs `CategoryEngine.categorize()`, persists result to `MessageMeta` |
| `recategorizeThread(threadId, category)` | Updates all messages in thread to new category + sets `isManualOverride = true` |

#### Blocking
| Method | Description |
|--------|-------------|
| `blockNumber(number)` | Inserts into `BlockedNumber` table |
| `unblockNumber(number)` | Removes from `BlockedNumber` table |
| `isBlocked(number)` | Checks existence |
| `observeBlockedNumbers()` | Returns `Flow<List<BlockedNumber>>` |

#### Backup
| Method | Description |
|--------|-------------|
| `createBackup()` | Creates encrypted backup record (MVP: stores metadata; full backup implementation extends this) |
| `observeBackups()` | Returns backup history as Flow |
| `getLatestBackup()` | One-shot latest backup query |

#### Search
| Method | Description |
|--------|-------------|
| `searchMessages(query)` | Performs `LIKE` query on `Telephony.Sms` body column, returns `Flow<List<SmsMessage>>` |

### Thread Loading Flow

```
loadAllThreads()
  ├── smsManager.getAllThreads()          ← Raw SMS threads
  ├── blockedNumberDao.getAll()           ← Filter out blocked
  ├── threadMetaDao.getPinnedThreadIds()  ← Pin status
  ├── threadMetaDao.getArchivedThreadIds()
  ├── threadMetaDao.getMutedThreadIds()
  ├── contactResolver.resolveContact()    ← Enrich with names
  └── [for each thread] getThreadCategory() ← Categorize via engine
```
