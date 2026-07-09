# New Message Screen (`ui/screens/newmessage/`)

Compose and send a new SMS or group MMS.

## `NewMessageViewModel.kt`

| Method | Action |
|--------|--------|
| `updateSearch(query)` | Search contacts as user types (≥1 char triggers search) |
| `selectRecipient(contact)` | Add recipient to list |
| `removeRecipient(contact)` | Remove recipient |

## `NewMessageScreen.kt`

```
┌─────────────────────────────────────┐
│ TopAppBar: Meseji Mpya  [←]         │
├─────────────────────────────────────┤
│ Kwa: [___________________________]  │
│ ⚠ Kutuma kwa watu wengi kunaweza    │
│   kugharimu zaidi (if >1 recipient)  │
├─────────────────────────────────────┤
│ Contact suggestions (live search):   │
│ 👤 James Mwangi                     │
│     +255 712 345 678                │
│ 👤 Sarah Komba                      │
│     +255 765 432 101                │
│ ...                                 │
└─────────────────────────────────────┘
```

### Implementation Notes

- The "To:" field uses an `OutlinedTextField` with live search
- Contact search is performed via `ContactResolver.searchContacts()`
- Selecting multiple recipients triggers the group MMS warning
- The actual message composer and send button are handled by navigating to the Chat screen after selecting a recipient (or will be extended with a full inline composer)
