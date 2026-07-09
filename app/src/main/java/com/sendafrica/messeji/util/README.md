# Utilities (`util/`)

Helper classes used across the application.

## `ContactResolver.kt`

Wraps `ContactsContract.PhoneLookup` and `CommonDataKinds.Phone` ContentResolver queries.

| Method | Description |
|--------|-------------|
| `resolveContact(number)` | Look up a single phone number → `ContactInfo` |
| `searchContacts(query)` | Search by name or number → `List<ContactInfo>` |
| `getAllContacts()` | All device contacts sorted by name |
| `isContact(number)` | Boolean check if number is in contacts |

**Deduplication**: `searchContacts()` and `getAllContacts()` track seen contact IDs to avoid returning multiple phone numbers for the same contact.

## `TimeFormatter.kt`

Relative and absolute time formatting for message timestamps.

| Method | Output Example |
|--------|---------------|
| `formatRelative(timestamp)` | "Sasa hivi", "Dakika 5", "10:42", "Jana", "Jan 15" |
| `formatMessageTime(timestamp)` | "10:42" (HH:mm) |
| `formatDateHeader(timestamp)` | "Leo", "Jana", "Januari 15, 2024" |

**Logic**:
- < 1 minute → "Sasa hivi"
- < 1 hour → "Dakika X"
- Today → "HH:mm"
- Yesterday → "Jana"
- Older → "MMM d" or "MMMM d, yyyy"

Uses `java.util.Locale("sw")` for Swahili month names.
