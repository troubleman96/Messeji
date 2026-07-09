# Settings Screens (`ui/screens/settings/`)

Application settings with main menu and sub-screens. Built by Camel Creatives.

## `SettingsScreen.kt`

Settings menu list with navigation to each sub-screen. Items:

| Item | Icon | Navigates To |
|------|------|-------------|
| Arifa (Notifications) | 🔔 | `NotificationsScreen` |
| Muonekano (Appearance) | 🎨 | `AppearanceScreen` |
| Kadi za SIM (SIM cards) | 💳 | `SimScreen` |
| Namba Zilizozuiwa (Blocked) | 🚫 | `BlockedNumbersScreen` |
| Nakala Rudufu (Backup) | 💾 | `BackupRestoreScreen` |
| Ulinzi wa Programu (App Lock) | 🔒 | `LockSetupScreen` |
| Data na Hifadhi (Data & Storage) | 💿 | Data screen (future) |
| Unganisha na Kompyuta (PC) | 📷 | "Inakuja Hivi Karibuni" |
| Kuhusu (About) | ℹ️ | `AboutScreen` |
| Msaada (Help) | ❓ | Help screen |

## `SettingsSubScreens.kt`

Three sub-screens in one file:

### NotificationsScreen
- Master toggle: Washa Arifa
- Per-category toggles: Mtu kwa Mtu, Pesa na OTP, Matangazo
- Vibration toggle
- Lock screen preview toggle
- **Security-conscious default**: Pesa na OTP lock screen preview is OFF by default

### AppearanceScreen
- Theme selector: Mwanga (Light) / Giza (Dark) / Mfumo (System)
- Radio-button style selection (implemented as Switch per option)

### SimScreen
- Default SIM selector for sending SMS
- Simple toggle between SIM 1 and SIM 2
