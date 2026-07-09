# Background Services (`service/`)

Handles system-level events: incoming SMS, device boot, and notification reliability. Built by Camel Creatives.

## Components

### `SmsReceiver.kt` — Incoming SMS Handler

`BroadcastReceiver` that intercepts incoming SMS when Messeji is set as the default SMS app.

| Intent Filter | Action |
|---------------|--------|
| `SMS_DELIVER_ACTION` | Incoming SMS (only default app receives this) |
| `WAP_PUSH_DELIVER_ACTION` | Incoming MMS (future) |

**Priority**: `1000` (highest) to ensure Messeji processes the message before any other receiver.

#### Processing Flow:
```
onReceive()
  → Telephony.Sms.Intents.getMessagesFromIntent()
  → For each SmsMessage:
    ├── categoryEngine.categorize(sender, body, isContact)
    ├── messageMetaDao.upsert(MessageMeta with category)
    └── notificationManager.showMessageNotification()
```

**Note on `getOrCreateThreadId()`**: Since the SMS may not yet be in the Telephony provider when the broadcast fires, we generate a temporary thread ID from the sender address hash. The actual thread ID is assigned by the system when the message is written to the provider.

### `BootReceiver.kt` — Device Boot

Re-registers notification channels after device restart (channels persist, but re-registration is idempotent and harmless).

### `NotificationService.kt` — Foreground Service

Currently a minimal service stub. On aggressive OEM battery-optimization skins (Tecno/Infinix/itel HiOS, Xiaomi MIUI), a foreground service may be needed for notification reliability. This is expanded when the onboarding battery-optimization tip is implemented:

> "Baadhi ya simu huzuia arifa. Bofya hapa kuruhusu Messeji ifanye kazi vizuri nyuma."
> (Some phones block notifications — tap here to allow Messeji to run properly in the background.)
