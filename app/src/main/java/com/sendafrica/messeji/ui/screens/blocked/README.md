# Blocked Numbers Screen (`ui/screens/blocked/`)

## `BlockedNumbersScreen.kt`

View and manage blocked phone numbers. Built by Camel Creatives.

```
┌─────────────────────────────────────┐
│ TopAppBar: Namba Zilizozuiwa [←]   │
├─────────────────────────────────────┤
│                                     │
│ 🚫 +255 712 345 678    [Fungua]    │
│ 🚫 TIGOPESA            [Fungua]    │
│ 🚫 15092               [Fungua]    │
│                                     │
│ Empty state:                        │
│ "Hakuna namba zilizozuiwa."        │
│                                     │
└─────────────────────────────────────┘
```

### Features

- **Live list**: Observes `BlockedNumberDao.observeAll()` via Flow
- **Unblock**: Each row has a "Fungua" (Unblock) button that calls `MessageRepository.unblockNumber()`
- **Empty state**: Shows "Hakuna namba zilizozuiwa." when list is empty
- **Block icon**: Red Alert-colored block icon for each entry
- Numbers are blocked via the Chat screen overflow menu or Home screen multi-select actions
