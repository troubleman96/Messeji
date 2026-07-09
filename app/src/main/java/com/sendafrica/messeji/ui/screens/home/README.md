# Home Screen (`ui/screens/home/`)

The main conversation list — the default landing tab after onboarding. Built by Camel Creatives.

## `HomeViewModel.kt`

### State: `HomeUiState`

| Field | Type | Description |
|-------|------|-------------|
| `threads` | `List<ThreadInfo>` | All loaded conversations |
| `selectedFilter` | String | Current filter chip: "zote", "mtu_kwa_mtu", "pesa_na_otp", "matangazo" |
| `isLoading` | Boolean | Loading indicator |
| `error` | String? | Error message |
| `selectedThreadIds` | `Set<Long>` | Multi-select state |
| `isMultiSelectMode` | Boolean | Batch action mode |

### Key Methods

| Method | Action |
|--------|--------|
| `loadThreads()` | Reload from repository, re-apply filters |
| `setFilter(filter)` | Update active filter chip |
| `getFilteredThreads()` | Returns threads matching current filter |
| `enterMultiSelectMode()` | Enable long-press selection mode |
| `toggleThreadSelection(id)` | Select/deselect a thread |
| `deleteSelectedThreads()` | Bulk delete with confirmation |
| `togglePinThread(id, pinned)` | Pin/unpin |
| `toggleMuteThread(id, muted)` | Mute/unmute |
| `archiveThread(id)` | Archive single thread |
| `blockNumber(number)` | Block and refresh |

## `HomeScreen.kt`

### Layout

```
┌─────────────────────────────────────┐
│ TopAppBar: Messeji | 🔍 | ⋮       │
├─────────────────────────────────────┤
│ FilterChips: Zote · Mtu kwa Mtu ·  │
│             Pesa na OTP · Matangazo │
├─────────────────────────────────────┤
│ Multi-select bar (visible when      │
│ active): [Hifadhi] [Futa]           │
├─────────────────────────────────────┤
│ LazyColumn:                         │
│ ┌─────────────────────────────────┐ │
│ │ [Av] Name            Timestamp │ │
│ │      Message preview...   ●    │ │
│ ├─────────────────────────────────┤ │
│ │ ...more threads                 │ │
│ └─────────────────────────────────┘ │
├─────────────────────────────────────┤
│               FAB: [+]              │
└─────────────────────────────────────┘
```

### Interaction Details

- **Single tap thread** → Opens Chat screen
- **Long-press thread** → Enters multi-select mode with that thread selected
- **Swipe right** → Archive (future: gesture-based, currently via multi-select)
- **Swipe left** → Delete with confirmation dialog
- **Filter chips** → Filter the thread list by category in real-time
- **Overflow menu** → Chagua (select), Hifadhi Zote (archive all read), Mipangilio
- **Empty state** → Friendly message per filter, with "Meseji Mpya" CTA on "Zote" filter
