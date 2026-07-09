# Chat Screen (`ui/screens/chat/`)

The individual message thread view — send, receive, and manage a conversation. Built by Camel Creatives.

## `ChatViewModel.kt`

### State: `ChatUiState`

| Field | Type | Description |
|-------|------|-------------|
| `messages` | `List<ChatMessage>` | All messages in thread |
| `inputText` | String | Composer text field |
| `isLoading` | Boolean | Loading indicator |
| `isSending` | Boolean | Send in progress |
| `error` | String? | Error message |
| `contactName` | String | Display name for header |
| `address` | String | Phone number for sending |
| `selectedSim` | Int | Active SIM slot (0 = default) |

### Message Status Mapping

SMS protocol status values mapped to UI indicators:

| Status Value | UI Indicator | Meaning |
|-------------|--------------|---------|
| 0 (STATUS_NONE) | ○ | Sending / pending |
| 64 (STATUS_COMPLETE) | ✓ | Sent |
| 128 (STATUS_PENDING) | ✓✓ | Delivered (where carrier confirms) |
| Other | ⚠ | Failed |

### Key Methods

| Method | Action |
|--------|--------|
| `loadThread(threadId, address, name)` | Load messages, mark thread read |
| `sendMessage()` | Send via `MessageRepository` with SIM selection |
| `setSimSlot(slot)` | Switch SIM for this thread |
| `refreshMessages()` | Reload message list |
| `deleteMessage(id)` | Delete single message |
| `updateInput(text)` | Update composer text |

## `ChatScreen.kt`

### Layout

```
┌─────────────────────────────────────┐
│ TopAppBar: Name   [📞] [⋮]         │
│            SIM 2                     │
├─────────────────────────────────────┤
│ LazyColumn (messages):              │
│                                     │
│          ┌──────────────────┐       │
│          │ Received message │       │
│          │          10:42   │       │
│          └──────────────────┘       │
│ ┌──────────────────┐               │
│ │ Sent message     │               │
│ │ 10:43 ✓         │               │
│ └──────────────────┘               │
│                                     │
├─────────────────────────────────────┤
│ SIM picker bar (optional)            │
│ [📱] [Andika meseji...]   [160] [➤]│
└─────────────────────────────────────┘
```

### Features

- **Auto-scroll** to bottom on new messages (with `LaunchedEffect`)
- **Date headers** ("Leo", "Jana", date) between messages
- **Message bubbles** styled differently for sent vs received
- **Long-press message** → Context menu: Nakili, Peleka Mbele, Futa, Taarifa
- **SIM indicator** in top bar, tappable to switch
- **Character counter** appears when approaching 160-char boundary, shows SMS segment count
- **Overflow menu**: Zuia (Block), Nyamazisha (Mute), Futa Mazungumzo (Delete thread)
