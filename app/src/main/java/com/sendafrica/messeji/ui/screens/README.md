# Screens (`ui/screens/`)

Each sub-directory contains one screen or feature module with its own ViewModel.
Every screen follows the pattern: `{Name}Screen.kt` (UI) + `{Name}ViewModel.kt` (state).

## Screen Index

| Screen | Package | Purpose |
|--------|---------|---------|
| Onboarding | `onboarding/` | 6-step first-run flow (welcome, permissions, default app, lock) |
| Home | `home/` | Conversation list with filter chips, multi-select, FAB |
| Chat | `chat/` | Message thread with send/receive, SIM picker, context menu |
| New Message | `newmessage/` | Composer with contact search and multiple recipients |
| Contacts | `contacts/` | Device contact list with alphabet index + search |
| Contact Detail | `contacts/` | Single contact view with message/call/block actions |
| Search | `search/` | Global search across messages and contacts |
| Settings | `settings/` | Main settings menu + sub-screens (notifications, appearance, etc.) |
| Lock | `lock/` | PIN entry screen + lock setup (PIN/biometric/auto-lock) |
| Backup | `backup/` | Backup now, auto-backup toggle, restore |
| Blocked | `blocked/` | Blocked numbers list with unblock action |
| About | `about/` | App info, version, Send Africa branding |

## Common UI Patterns

- **TopAppBar with Primary background**: Consistent across all screens
- **Swahili-only text**: All visible text comes from `strings.xml` resources
- **Loading/Error/Empty states**: Each screen handles loading, error, and empty states gracefully
- **Material 3 components**: Cards, Chips, Dialogs, Snackbars used throughout
