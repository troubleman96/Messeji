# Search Screen (`ui/screens/search/`)

Global search across message content and contacts.

## `SearchViewModel.kt`

| Method | Action |
|--------|--------|
| `updateQuery(query)` | Set search query, trigger debounced search |

### Search Implementation

- **Minimum query length**: 2 characters
- **Debounce**: 300ms delay before executing search (avoids excessive queries during typing)
- **Dual search**: Contacts searched via `ContactResolver.searchContacts()`, messages via `MessageRepository.searchMessages()` (SQL `LIKE` query on `body` column)
- **Contact results** shown first under "Anwani" header
- **Message results** shown under "Meseji" header with contact name, snippet, and timestamp

## `SearchScreen.kt`

```
┌─────────────────────────────────────┐
│ TopAppBar: Tafuta  [←]              │
├─────────────────────────────────────┤
│ [🔍 Tafuta meseji na anwani...]     │
├─────────────────────────────────────┤
│                                     │
│ Anwani                              │
│ ┌─────────────────────────────────┐ │
│ │ 👤 James     James Mwangi      │ │
│ ├─────────────────────────────────┤ │
│ │ 👤 Sarah     Sarah Komba       │ │
│ └─────────────────────────────────┘ │
│                                     │
│ Meseji                              │
│ ┌─────────────────────────────────┐ │
│ │ 👤 James  Salaam, habari?  Jan │ │
│ │ 👤 M-PESA  Umepokea TSh...  Leo│ │
│ └─────────────────────────────────┘ │
└─────────────────────────────────────┘
```
