# Notification System (`notification/`)

Handles all notification display for incoming SMS messages. Uses Android NotificationCompat with per-category channels.

## Components

### `NotificationChannels.kt`
Creates 4 notification channels (required for Android 8.0+):

| Channel ID | Name (Swahili) | Importance | Badge | Vibration |
|------------|----------------|------------|-------|-----------|
| `mtu_kwa_mtu` | Mtu kwa Mtu | HIGH | Yes | Yes |
| `pesa_otp` | Pesa na OTP | HIGH | Yes | Yes |
| `matangazo` | Matangazo | LOW | No | No |
| `service` | Huduma | MIN | No | No |

Called on app startup and on device boot (via `BootReceiver`).

### `MessageNotificationManager.kt`
Builds and displays notifications for incoming SMS.

#### Features

| Feature | Implementation |
|---------|---------------|
| **Per-category toggles** | Checks `AppSettings` flags before showing — users can mute Matangazo entirely |
| **MessagingStyle** | Uses `NotificationCompat.MessagingStyle` with `Person` for rich, conversation-style notifications |
| **Quick Reply** | "Jibu" action button with `RemoteInput` for inline reply |
| **Mark Read** | "Soma" action button that opens the thread |
| **Grouping** | Notifications from the same thread are grouped using `setGroup()` |
| **Priority** | Promo notifications use `PRIORITY_LOW`; person and money/OTP use `PRIORITY_HIGH` |

#### Security Note
Lock screen preview is controlled by `AppSettings.notifShowLockscreen` and `notifShowLockscreenPesaOtp`. By default, Pesa na OTP notifications do **not** show content on the lock screen to prevent OTP theft via lock screen — this is a deliberate security choice unique to this app.

#### Notification Flow
```
SmsReceiver.onReceive()
  → CategoryEngine.categorize()
  → MessageNotificationManager.showMessageNotification()
    ├── Check AppSettings: is this category enabled?
    ├── Build Person (sender)
    ├── Build MessagingStyle notification
    ├── Add Reply/MarkRead actions
    ├── Set group for thread bundling
    └── notify()
```
