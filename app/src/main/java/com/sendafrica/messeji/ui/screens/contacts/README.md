# Contacts Screens (`ui/screens/contacts/`)

Device contacts browsing and detail view. Built by Camel Creatives.

## `ContactsViewModel.kt`

| Method | Action |
|--------|--------|
| `loadContacts()` | Load all device contacts via `ContactResolver.getAllContacts()` |
| `updateSearch(query)` | Filter contacts by name or phone number |

## `ContactsScreen.kt`

```
┌─────────────────────────────────────┐
│ TopAppBar: Anwani  [←]              │
├─────────────────────────────────────┤
│ [🔍 Tafuta anwani...]               │
├─────────────────────────────────────┤
│ LazyColumn:                         │
│ 👤 James Mwangi                     │
│     +255 712 345 678                │
│ 👤 Sarah Komba                      │
│     +255 765 432 101                │
│ ...                                 │
└─────────────────────────────────────┘
```

- Search-as-you-type with immediate filtering
- Tap contact → Contact Detail screen
- Shows name + phone number per row

## `ContactDetailScreen.kt`

```
┌─────────────────────────────────────┐
│ TopAppBar: [←] Name                 │
├─────────────────────────────────────┤
│          [👤]                       │
│         Name                        │
│     +255 712 345 678                │
│                                     │
│  ┌──────────────────────────────┐   │
│  │ ✉ Tuma Meseji                │   │
│  └──────────────────────────────┘   │
│  ┌──────────────────────────────┐   │
│  │ 📞 Piga Simu                 │   │
│  └──────────────────────────────┘   │
│  ┌──────────────────────────────┐   │
│  │ 🚫 Zuia                      │   │
│  └──────────────────────────────┘   │
└─────────────────────────────────────┘
```

- Large avatar (generated initials or contact photo)
- Display name + all phone numbers
- Three action buttons: Send Message, Call (opens dialer), Block
- Block button uses `Alert` red color via `OutlinedButton`
