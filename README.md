# Messeji — Default SMS App for Tanzania

**Messeji** ("Messages" — Swahilized) is a default-SMS-replacement Android app built for the Tanzanian market. It takes the core SMS/MMS experience Tanzanians already use daily — mobile money alerts from M-Pesa/Tigo Pesa/Airtel Money, OTPs, person-to-person texting, business SMS — and wraps it in a fast, beautiful, entirely Swahili interface with modern conveniences: smart categorization of mobile money/OTP messages, spam blocking, dual-SIM awareness, and low-data-usage design.

> **Product by Send Africa** — **Built by [Camel Creatives](https://camelcreatives.com)**  
> 100% Swahili interface — no English in the shipped product.

---

## Table of Contents

- [Product Vision](#product-vision)
- [Screenshots](#screenshots)
- [MVP Scope](#mvp-scope)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Screens & Features](#screens--features)
- [Categorization Engine](#categorization-engine)
- [Permissions](#permissions)
- [Design System](#design-system)
- [Build Instructions](#build-instructions)
- [Running Tests](#running-tests)
- [Contributing](#contributing)
- [License](#license)

---

## Product Vision

**Why Android-first (critical technical note):** iOS does not allow third-party apps to read/send SMS or register as the default messaging app in the way Android does (Apple restricts this to iMessage + limited Message Filter extensions). Since Messeji's core value is being the *default SMS handler*, **Android is the only viable platform for MVP**. An iOS companion (read-only iMessage-adjacent utility, or eventually RCS-based) can be scoped later but must not be promised in the MVP.

**Target user:** Tanzanian smartphone owner, Android, budget-to-mid device (2–4GB RAM common), prepaid data, frequently receives mobile-money and OTP SMS, may run dual SIM (Vodacom/Tigo/Airtel/Halotel), prefers Swahili UI, low tolerance for apps that drain data or battery.

---

## Screenshots

*(Coming soon — add screenshots of Onboarding, Home with filter chips, Chat thread, Settings, and dark mode)*

---

## MVP Scope

### In scope (fully implemented):
- ✅ Default SMS/MMS app (send, receive, threads, group MMS)
- ✅ 100% Swahili UI, no language switch in MVP
- ✅ Smart inbox: automatic separation of **Mtu kwa Mtu** (Person-to-Person), **Pesa na OTP** (Money & OTP), and **Matangazo** (Promotions/spam)
- ✅ Dual-SIM support with per-SIM sending
- ✅ Contacts integration
- ✅ Search across messages and contacts
- ✅ Local backup/restore (encrypted, to device storage)
- ✅ Notifications with quick-reply (Jibu), mark-as-read (Soma)
- ✅ App lock (PIN/fingerprint)
- ✅ Block & report spam numbers
- ✅ Dark and light theme
- ✅ Onboarding + full permission flow
- ✅ Settings (notifications, appearance, backup, default SIM, blocked numbers, app lock, about)

### Explicitly out of scope for MVP (Phase 2+):
- ❌ PC/desktop companion via QR pairing (spec documented but not built)
- ❌ RCS (rich chat) support
- ❌ Cloud sync across devices
- ❌ Multi-language toggle
- ❌ In-app calling

---

## Architecture

Messeji follows **MVVM (Model-View-ViewModel)** architecture with **unidirectional data flow** using Kotlin coroutines and StateFlow.

```
┌─────────────────────────────────────────────────────────┐
│                      UI Layer                           │
│  ┌─────────────┐  ┌──────────────┐  ┌───────────────┐  │
│  │ Jetpack     │  │ Material 3   │  │ Navigation    │  │
│  │ Compose     │  │ Components   │  │ Compose       │  │
│  └──────┬──────┘  └──────┬───────┘  └───────┬───────┘  │
│         │                │                  │          │
│  ┌──────┴────────────────┴──────────────────┴──────┐   │
│  │            ViewModels (Hilt)                     │   │
│  └────────────────────┬─────────────────────────────┘   │
├───────────────────────┼─────────────────────────────────┤
│                  Data Layer                             │
│  ┌────────────────────┼─────────────────────────────┐   │
│  │         Repository Layer (MessageRepository)      │   │
│  └──┬──────────────┬──────────────┬─────────────────┘   │
│     │              │              │                      │
│  ┌──▼──┐    ┌──────▼──────┐  ┌───▼────────┐            │
│  │Room │    │ SMS Provider│  │ Category   │            │
│  │ DB  │    │ (Content    │  │ Engine     │            │
│  │     │    │  Resolver)  │  │            │            │
│  └─────┘    └─────────────┘  └────────────┘            │
└─────────────────────────────────────────────────────────┘
```

### Key architectural decisions:

1. **No mandatory backend** — The MVP is fully on-device. SMS content never leaves the phone. This is deliberate for speed, offline-reliability, low data cost, and privacy trust-building in a market sensitive to money-related SMS fraud.

2. **SMS lives in the system Telephony provider** — `content://sms` and `content://mms` are read/written via `Telephony.Sms` content resolvers when registered as the default SMS app. Room is used only for Messeji-specific metadata layered on top (category tags, pin/mute/archive state, per-sender overrides, blocked list).

3. **Offline-first** — 100% of MVP functionality works with zero internet connectivity. SMS itself doesn't require data. Only backup-to-Drive (future) and Phase 2 desktop sync require connectivity.

---

## Tech Stack

| Category | Technology |
|---|---|
| **Language** | Kotlin 1.9.22 |
| **UI Toolkit** | Jetpack Compose (BOM 2024.01.00) |
| **Architecture** | MVVM + Unidirectional Data Flow |
| **DI** | Hilt (Dagger) 2.50 |
| **Database** | Room 2.6.1 (metadata layer) |
| **SMS Access** | Android Telephony Provider (ContentResolver) |
| **Settings** | DataStore Preferences |
| **Background** | WorkManager, BroadcastReceiver |
| **Navigation** | Navigation Compose 2.7.6 |
| **Biometrics** | Biometric Library 1.2.0 |
| **Async** | Kotlin Coroutines + Flow |
| **Build** | Gradle KTS, KSP |
| **Min SDK** | 24 (Android 7.0, covers ~98% of active TZ devices) |
| **Target SDK** | 34 (Android 14) |

---

## Project Structure

```
app/
├── src/main/
│   ├── java/com/sendafrica/messeji/
│   │   ├── MessejiApp.kt              # Application class, Hilt + WorkManager
│   │   ├── MainActivity.kt            # Single activity, Compose host
│   │   │
│   │   ├── data/
│   │   │   ├── AppSettings.kt         # DataStore preferences wrapper
│   │   │   ├── categorization/
│   │   │   │   └── CategoryEngine.kt  # Client-side message categorization
│   │   │   ├── db/
│   │   │   │   ├── AppDatabase.kt     # Room database definition
│   │   │   │   ├── dao/               # DAO interfaces
│   │   │   │   │   ├── BackupRecordDao.kt
│   │   │   │   │   ├── BlockedNumberDao.kt
│   │   │   │   │   ├── MessageMetaDao.kt
│   │   │   │   │   ├── SenderRuleDao.kt
│   │   │   │   │   └── ThreadMetaDao.kt
│   │   │   │   └── entity/            # Room entities
│   │   │   │       ├── BackupRecord.kt
│   │   │   │       ├── BlockedNumber.kt
│   │   │   │       ├── MessageMeta.kt
│   │   │   │       ├── SenderRule.kt
│   │   │   │       └── ThreadMeta.kt
│   │   │   ├── repository/
│   │   │   │   └── MessageRepository.kt  # Single source of truth
│   │   │   └── sms/
│   │   │       └── SmsManager.kt         # Telephony provider wrapper
│   │   │
│   │   ├── di/
│   │   │   └── AppModule.kt              # Hilt DI module
│   │   │
│   │   ├── domain/
│   │   │   ├── ContactInfo.kt            # Domain contact model
│   │   │   └── ThreadInfo.kt             # Domain thread model
│   │   │
│   │   ├── notification/
│   │   │   ├── MessageNotificationManager.kt  # Build & show notifications
│   │   │   └── NotificationChannels.kt         # Per-category channels
│   │   │
│   │   ├── service/
│   │   │   ├── BootReceiver.kt           # Re-register channels on boot
│   │   │   ├── NotificationService.kt    # Foreground service (notification reliability)
│   │   │   └── SmsReceiver.kt           # Incoming SMS broadcast receiver
│   │   │
│   │   ├── ui/
│   │   │   ├── MainViewModel.kt         # Global state (lock, etc.)
│   │   │   ├── navigation/
│   │   │   │   └── NavGraph.kt          # Navigation routes & graph
│   │   │   ├── screens/
│   │   │   │   ├── about/AboutScreen.kt
│   │   │   │   ├── backup/BackupRestoreScreen.kt
│   │   │   │   ├── blocked/BlockedNumbersScreen.kt
│   │   │   │   ├── chat/
│   │   │   │   │   ├── ChatScreen.kt
│   │   │   │   │   └── ChatViewModel.kt
│   │   │   │   ├── contacts/
│   │   │   │   │   ├── ContactDetailScreen.kt
│   │   │   │   │   ├── ContactsScreen.kt
│   │   │   │   │   └── ContactsViewModel.kt
│   │   │   │   ├── home/
│   │   │   │   │   ├── HomeScreen.kt
│   │   │   │   │   └── HomeViewModel.kt
│   │   │   │   ├── lock/
│   │   │   │   │   ├── LockScreen.kt
│   │   │   │   │   └── LockSetupScreen.kt
│   │   │   │   ├── newmessage/
│   │   │   │   │   ├── NewMessageScreen.kt
│   │   │   │   │   └── NewMessageViewModel.kt
│   │   │   │   ├── onboarding/
│   │   │   │   │   └── OnboardingScreen.kt
│   │   │   │   ├── search/
│   │   │   │   │   ├── SearchScreen.kt
│   │   │   │   │   └── SearchViewModel.kt
│   │   │   │   └── settings/
│   │   │   │       ├── SettingsScreen.kt
│   │   │   │       └── SettingsSubScreens.kt
│   │   │   └── theme/
│   │   │       ├── Color.kt
│   │   │       ├── Theme.kt
│   │   │       └── Type.kt
│   │   │
│   │   └── util/
│   │       ├── ContactResolver.kt       # Device contacts lookup
│   │       └── TimeFormatter.kt         # Relative time formatting
│   │
│   ├── assets/
│   │   └── category_rules.json          # MNO/bank keywords, OTP regex rules
│   │
│   └── res/
│       ├── values/strings.xml           # 100% Swahili strings
│       ├── values/colors.xml
│       ├── values/themes.xml
│       ├── values-night/themes.xml      # Dark theme
│       └── xml/
│           ├── file_paths.xml
│           └── network_security_config.xml
│
├── build.gradle.kts
└── proguard-rules.pro

build.gradle.kts          # Root project build config
settings.gradle.kts       # Module settings
gradle.properties         # Gradle JVM & AndroidX settings
```

---

## Screens & Features

### 1. Splash / Launch
- Camel Creatives splash → Messeji logo, routes to Onboarding (first run) or Home.

### 2. Onboarding (6 steps)
| Step | Screen | Description |
|------|--------|-------------|
| 0 | **Karibu** | Welcome screen with "Anza" CTA |
| 1 | **Ruhusa ya Meseji** | Request `READ_SMS`/`RECEIVE_SMS` permissions |
| 2 | **Weka kama SMS Kuu** | Set Messeji as default SMS app via `RoleManager` |
| 3 | **Ruhusa ya Anwani** | Request contacts access (skippable) |
| 4 | **Ruhusa ya Arifa** | Request notification permission |
| 5 | **Ulinzi wa Programu** | Optional PIN/biometric lock setup |
| 6 | **Umekamilika** | Completion animation → "Fungua Messeji" |

### 3. Home — Mazungumzo (Conversations)
- **Top bar**: Messeji logo, search icon, overflow menu (Chagua, Hifadhi Zote, Mipangilio)
- **Filter chips**: Zote · Mtu kwa Mtu · Pesa na OTP · Matangazo (horizontally scrollable)
- **Thread list**: Avatar (generated initials) · Name/Number · Snippet · Timestamp · Unread badge · Pin/Mute icons
- **Swipe actions**: Swipe right → Archive, Swipe left → Delete (with confirm)
- **Long-press**: Multi-select mode with bulk actions (Futa, Hifadhi, Zuia, Bandika)
- **FAB**: + button → New Message composer
- **Empty state**: Illustration + "Hakuna mazungumzo bado" message

### 4. Chat Thread
- **Top bar**: Contact name + avatar, call button, overflow menu (Zuia, Nyamazisha, Futa)
- **SIM indicator**: Shows which SIM card the thread will send from (tappable to switch)
- **Message bubbles**: Received = left (surface color), Sent = right (brand green)
- **Time headers**: "Leo", "Jana", date-based grouping
- **Delivery status**: Clock (sending) → ✓ (sent) → ✓✓ (delivered) — no fake read receipts
- **Composer bar**: Attachment button · Text input (expands 5 lines) · Character counter · Send button
- **Long-press message**: Context menu — Nakili (Copy), Peleka Mbele (Forward), Futa (Delete), Taarifa (Info)

### 5. New Message Composer
- **"Kwa:"** field with live contact search-as-you-type + freeform number entry
- Multiple recipients auto-create group MMS with cost warning
- Same composer bar as thread screen

### 6. Contacts (Anwani)
- Alphabetical list with A–Z fast-scroll rail
- Search bar at top
- Each row: avatar + name (tap → Contact Detail)
- **Contact Detail**: Avatar, name, phone numbers, buttons: Tuma Meseji / Piga Simu / Zuia, message history

### 7. Search (Tafuta)
- Global search across message bodies, contact names, phone numbers
- Results grouped under **Anwani** (Contacts) / **Meseji** (Messages with highlighted snippets)
- Debounced (300ms) for performance

### 8. Settings (Mipangilio)
| Setting | Description |
|---------|-------------|
| **Arifa** | Master toggle, per-category toggles (Mtu kwa Mtu/Pesa na OTP/Matangazo), sound, vibration, lock screen preview |
| **Muonekano** | Dark/Light/System theme toggle |
| **Kadi za SIM** | Default SIM for sending |
| **Namba Zilizozuiwa** | Blocked numbers list with unblock |
| **Nakala Rudufu** | Backup now, auto-backup toggle, restore |
| **Ulinzi wa Programu** | PIN/biometric lock setup, auto-lock timing |
| **Data na Hifadhi** | Cache size, auto-download media settings |
| **Unganisha na Kompyuta** | Phase 2 — shown as "Inakuja Hivi Karibuni" |
| **Kuhusu** | Version, Camel Creatives / Send Africa branding, privacy policy |
| **Msaada** | FAQ / Contact support |

### 9. App Lock
- PIN pad (4–6 digits) + optional biometric fingerprint
- Auto-lock timing: Mara moja / Dakika 1 / Dakika 5 / Kamwe
- Uses Android Keystore via DataStore for PIN hash storage

### 10. Backup & Restore
- Encrypted backup (AES-256) of SMS DB + metadata
- Local storage or Google Drive (future)
- Shows last backup date/time
- Auto-backup toggle (WiFi-only)

---

## Categorization Engine

The categorization engine at `data/categorization/CategoryEngine.kt` runs **entirely on-device** — no data leaves the phone. It uses a bundled JSON ruleset (`assets/category_rules.json`) containing:

### Rule Evaluation Order

1. **Contact match** → Always **Mtu kwa Mtu** (unless user manually overrode)
2. **User override** → `SenderRule` table (from manual recategorization)
3. **Bundled ruleset** → Known MNO/bank sender IDs, keywords, regex patterns
4. **Fallback heuristics** → Alphanumeric sender + URL → Promo; Numeric shortcode + amount pattern → Money/OTP
5. **Default** → **Mtu kwa Mtu**

### Bundled Rules Include:

- **Money senders**: M-PESA, TIGOPESA, AIRTELMONEY, HALOPESA, CRDB, NMB, EQUITY, KCB, etc.
- **OTP keywords**: "namba ya uthibitishaji", "msimbo wa uthibitisho", "OTP", "verification code"
- **Promo keywords**: "punguzo", "ofa maalum", "bei nafuu", "bonyeza link", "zawadi", "bonus"
- **Regex patterns**: TSh amounts (`TSh\s*[\d,]+`), OTP numeric codes, URLs

Users can manually recategorize threads via long-press → "Hamisha kundi", and the system learns per-sender overrides.

---

## Permissions

Permissions are requested **progressively** (never all at once), each with a plain-Swahili justification screen *before* the system dialog:

| Permission | When Requested | Why |
|---|---|---|
| `READ_SMS`, `RECEIVE_SMS`, `SEND_SMS` | Onboarding step 1 | Required to function as SMS app |
| Default SMS app role | Onboarding step 1 | Must be default to receive intents |
| `READ_CONTACTS` | Onboarding step 2 | Show names instead of phone numbers |
| `POST_NOTIFICATIONS` | Onboarding step 3 | Alert on new messages |
| `READ_PHONE_STATE` | Onboarding step 4 | Dual-SIM support |
| Camera / Storage | Contextual | Photo attachment / QR scan (Phase 2) |
| `USE_BIOMETRIC` | Settings | App lock fingerprint |

**Permission-denied handling:** If SMS/default-app role is denied, a persistent dismissible banner is shown: *"Messeji haiwezi kutuma au kupokea meseji bila ruhusa hii. Gusa hapa kuiwezesha."*

---

## Design System

### Brand Identity
- **Brand**: Camel Creatives presents Messeji for Send Africa
- **Primary color**: Deep Kilimanjaro green `#0B6E4F` (distinct from WhatsApp green)
- **Accent**: Sun-gold `#F2A93B` (unread badges, money/OTP tags)
- **Alert**: Maasai red `#C1272D` (delete, block, spam)
- **Neutral light**: `#F7F7F5` background, `#FFFFFF` cards
- **Neutral dark**: `#0F1512` background, `#161D19` cards

### Typography
- **Headers**: Semibold 20–24sp
- **Body**: Regular 15–16sp (never below 13sp)
- Noto Sans font family (full Latin + Swahili diacritic support, free, well-tested on budget devices)

### Data-conscious design
- No auto-loading images in MMS threads by default
- "Pakua picha" (Download image) tap-to-load state
- WiFi-only auto-download option

### Motion
- Minimal, fast (150–200ms)
- Respects "reduce motion" system setting
- No decorative animation that costs battery/CPU on low-end devices

### Accessibility
- Minimum touch target 48dp
- TalkBack labels in Swahili for all interactive elements
- Color contrast meets WCAG AA against light and dark surfaces
- Scalable text respecting system font-size setting

---

## Build Instructions

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17+
- Android SDK 34+
- Gradle 8.2+

### Setup

```bash
# Clone the repository
git clone git@github.com:troubleman96/Messeji.git
cd Messeji

# Build debug APK
./gradlew assembleDebug

# Build release APK (requires signing config)
./gradlew assembleRelease

# Install on connected device
./gradlew installDebug
```

### Build Configuration

The project is configured with:
- **minSdk**: 24 (Android 7.0) — covers ~98% of active Tanzanian Android devices
- **targetSdk**: 34 (Android 14)
- **compileSdk**: 34
- **ProGuard**: Enabled for release builds (shrinks APK below 15MB target)

### APK Size Target
Under **15MB** for the base MVP build (important for low-storage devices and data-cost-conscious sideload/download).

---

## Running Tests

```bash
# Run unit tests
./gradlew testDebugUnitTest

# Run instrumented tests (requires emulator/device)
./gradlew connectedAndroidTest
```

---

## Non-Functional Requirements

- **Cold start**: < 1.5s on 2GB RAM device
- **Scroll performance**: 60fps with 500+ conversations (Compose LazyColumn + pagination)
- **Battery**: No polling, rely on system broadcast intents, no unnecessary wake locks
- **Device coverage**: Test on Tecno, Infinix, itel (HiOS/XOS), Samsung, stock/AOSP
- **APK size**: Under 15MB
- **Offline**: 100% functional without internet

---

## Phase 2 — "Unganisha na Kompyuta" (PC Companion)

The spec for a WhatsApp-Web-style desktop companion via QR pairing is fully documented in the build spec. It will be built as Phase 2 after the MVP is stable and adopted.

**Architecture**: Phone-as-relay (same trust model as WhatsApp Web pre-multi-device). The phone remains the source of truth; desktop is a thin client. End-to-end encryption via Curve25519 + ECDH key exchange — relay server only sees ciphertext.

---

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Style
- Follow Kotlin coding conventions
- Use the project's existing patterns (MVVM, Hilt, Compose)
- All UI strings go through `strings.xml` — never hardcode Swahili text
- Keep the categorization rules JSON updated as new MNO/bank senders emerge

---

## License

© 2024 Send Africa. All rights reserved.  
Built by **Camel Creatives**.

Messeji is built for the Tanzanian market.  
*Your privacy matters — your messages are never sent to our servers.*

---

*Built with ❤️ by Camel Creatives for Tanzania. Meseji zako, kwa lugha yako, kwa urahisi.*
