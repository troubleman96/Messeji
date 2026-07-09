# MESSEJI — Full MVP Build Specification
**Product by Send Africa | SMS App for Tanzania | 100% Swahili Interface**

---

## 0. HOW TO USE THIS DOCUMENT

This is a complete product + engineering + design brief. Give it to a development team, or paste it into an AI coding agent (e.g. Claude Code) as the master prompt, then build screen-by-screen following the order in Section 6. Every screen has: purpose, layout, Swahili copy, states, and edge cases. Do not translate anything into English in the shipped product — English only appears in this document as clarifying notes in parentheses.

---

## 1. PRODUCT VISION

**Messeji** ("Messages" — Swahilized) is a default-SMS-replacement Android app built for the Tanzanian market. It takes the core SMS/MMS experience Tanzanians already use daily (mobile money alerts from M-Pesa/Tigo Pesa/Airtel Money, OTPs, person-to-person texting, business SMS) and wraps it in a fast, beautiful, entirely Swahili interface with modern conveniences: smart categorization of mobile money/OTP messages, spam blocking, dual-SIM awareness, low-data-usage design, and — in Phase 2 — a WhatsApp-Web-style PC companion unlocked via QR code.

**Why Android-first (critical technical note):** iOS does not allow third-party apps to read/send SMS or register as the default messaging app in the way Android does (Apple restricts this to iMessage + limited Message Filter extensions). Since Messeji's core value is being the *default SMS handler*, **Android is the only viable platform for MVP**. An iOS companion (read-only iMessage-adjacent utility, or eventually RCS-based) can be scoped later but must not be promised in the MVP.

**Target user:** Tanzanian smartphone owner, Android, budget-to-mid device (2–4GB RAM common), prepaid data, frequently receives mobile-money and OTP SMS, may run dual SIM (Vodacom/Tigo/Airtel/Halotel), prefers Swahili UI, low tolerance for apps that drain data or battery.

---

## 2. MVP SCOPE (What ships first)

**In scope:**
- Default SMS/MMS app (send, receive, threads, group MMS)
- 100% Swahili UI, no language switch in MVP
- Smart inbox: automatic separation of Mtu-kwa-Mtu (Person-to-Person), Pesa na OTP (Money & OTP), and Matangazo (Promotions/spam)
- Dual-SIM support with per-SIM sending
- Contacts integration
- Search across messages and contacts
- Local backup/restore (encrypted, to device storage / Google Drive)
- Notifications with quick-reply, mark-as-read, mute
- App lock (PIN/fingerprint)
- Block & report spam numbers
- Dark and light theme
- Onboarding + full permission flow
- Settings (notifications, appearance, backup, default SIM, blocked numbers, storage/data usage, about Send Africa)

**Explicitly out of scope for MVP (Phase 2+):**
- PC/desktop companion via QR pairing (full architecture specified below, build after MVP is stable)
- RCS (rich chat) support
- Cloud sync across devices
- Multi-language toggle
- In-app calling

---

## 3. DESIGN SYSTEM

### 3.1 Visual identity
- **Brand:** Send Africa presents Messeji. App icon: a stylized speech bubble in the shape of a Tanzanian sunburst/kanga-pattern motif — warm, local, not generic.
- **Primary color:** Deep Kilimanjaro green `#0B6E4F` (trust, national resonance, distinct from WhatsApp green `#25D366` to avoid confusion — use a deeper, more teal-leaning green).
- **Accent color:** Sun-gold `#F2A93B` (used for unread badges, CTAs, the money/OTP category tag).
- **Alert/danger:** Maasai red `#C1272D` (delete, block, spam).
- **Neutral surface (light):** `#F7F7F5` background, `#FFFFFF` cards.
- **Neutral surface (dark):** `#0F1512` background, `#161D19` cards.
- **Typography:** Use a font with full Latin + strong readability at small sizes (e.g. Inter or Noto Sans — Noto Sans is preferred since it renders Swahili diacritics and is Google's own font, free, well-tested on budget devices). Headers: Semibold 20–24sp. Body: Regular 15–16sp. Never below 13sp (accessibility for older users).
- **Iconography:** Rounded, filled-outline style, 24dp grid, consistent stroke width 2dp.
- **Motion:** Minimal, fast (150–200ms), no decorative animation that costs battery/CPU on low-end devices. Respect "reduce motion" system setting.
- **Data-consciousness as a design value:** no auto-loading images in MMS threads by default; show a "Pakua picha" (Download image) tap-to-load state.

### 3.2 Core Swahili terminology (use consistently everywhere — this is the app's controlled vocabulary)

| English concept | Swahili term used in-app |
|---|---|
| Messages (app name/section) | Meseji |
| Conversation/thread | Mazungumzo |
| Contacts | Anwani |
| New message | Meseji Mpya |
| Send | Tuma |
| Search | Tafuta |
| Settings | Mipangilio |
| Notifications | Arifa |
| Block | Zuia |
| Report spam | Ripoti Uchafuzi |
| Unread | Haijasomwa |
| Archive | Hifadhi |
| Delete | Futa |
| Mute | Nyamazisha |
| Pin (conversation) | Bandika |
| Attachment | Kiambatanisho |
| Backup | Nakala Rudufu |
| Restore | Rejesha |
| Permissions | Ruhusa |
| SIM card | Kadi ya SIM |
| Person-to-person inbox | Mtu kwa Mtu |
| Money & OTP inbox | Pesa na Nambari za Uthibitisho |
| Promotions/spam inbox | Matangazo |
| Lock (app) | Funga kwa Nenosiri |
| Draft | Rasimu |
| Today | Leo |
| Yesterday | Jana |
| Connect to computer (Phase 2) | Unganisha na Kompyuta |

---

## 4. PERMISSIONS

Requested progressively, never all at once, each with a plain-language Swahili justification screen *before* the system dialog fires (this pre-permission screen dramatically improves grant rates and is standard best practice).

| Permission | When requested | Swahili justification shown to user |
|---|---|---|
| `READ_SMS`, `RECEIVE_SMS`, `SEND_SMS` | Onboarding step 1, required to function | "Messeji inahitaji ruhusa ya kusoma na kutuma meseji ili iwe programu yako kuu ya SMS." |
| Default SMS app role (`RoleManager` / `Telephony.Sms.getDefaultSmsPackage`) | Onboarding step 1, same screen | "Weka Messeji kama programu yako kuu ya meseji." |
| `READ_CONTACTS`, `WRITE_CONTACTS` (optional write) | Onboarding step 2 | "Ili kuonyesha majina ya watu badala ya namba za simu." |
| `POST_NOTIFICATIONS` (Android 13+) | Onboarding step 3 | "Ili kukujulisha ukipokea meseji mpya." |
| `READ_PHONE_STATE` | Onboarding step 4, only if dual-SIM device detected | "Ili kukuruhusu kuchagua SIM ya kutumia unapotuma meseji." |
| Camera | Contextual, only when user taps "Unganisha na Kompyuta" (QR scan, Phase 2) or attaches a photo | "Ili kupiga picha au kusoma msimbo wa QR." |
| Storage/Media (`READ_MEDIA_IMAGES` etc.) | Contextual, on first attachment action | "Ili kutuma na kupokea picha na faili." |
| Biometric (`USE_BIOMETRIC`) | Contextual, when user enables app lock in Settings | not a runtime permission dialog issue, but confirm via system biometric prompt |

**Permission-denied handling:** If `READ_SMS`/default-app role is denied, show a persistent but dismissible banner: *"Messeji haiwezi kutuma au kupokea meseji bila ruhusa hii. Gusa hapa kuiwezesha."* — app remains browsable (read-only, empty) but non-functional until granted. Never hard-block the user from exiting the app.

---

## 5. INFORMATION ARCHITECTURE

```
Messeji
├── Onboarding (first-run only)
│   ├── Karibu (Welcome)
│   ├── Ruhusa (Permissions flow, step-by-step)
│   ├── Ingiza kama SMS Kuu (Set as default)
│   ├── Weka Nenosiri (Optional: set app lock)
│   └── Umekamilika (Done → Home)
├── Home (bottom nav, 3 tabs)
│   ├── Tab 1: Mazungumzo (Conversations) — default landing tab
│   │   ├── Sub-filter chips: Zote | Mtu kwa Mtu | Pesa na OTP | Matangazo
│   │   └── Thread list
│   ├── Tab 2: Anwani (Contacts)
│   └── Tab 3: Mipangilio (Settings)
├── Chat Thread screen (opens from a conversation)
├── New Message composer
├── Contact detail / New contact
├── Search (global, accessible via top bar icon)
├── Blocked numbers list
├── Backup & Restore
├── Notification settings
├── App lock setup
├── Unganisha na Kompyuta (Phase 2 — QR pairing)
└── Kuhusu Send Africa (About)
```

---

## 6. SCREEN-BY-SCREEN SPECIFICATION

### 6.1 Splash / Launch
- Send Africa wordmark fades to Messeji logo, <800ms, then routes to onboarding (first run) or Home.

### 6.2 Onboarding — Karibu (Welcome)
- Full-bleed illustration (Tanzanian-styled, warm colors), headline: **"Karibu Messeji"**, subtext: **"Meseji zako, kwa lugha yako, kwa urahisi."** (Your messages, in your language, made simple.)
- Single CTA button: **"Anza"** (Start)

### 6.3 Onboarding — Ruhusa (Permissions)
- Sequential cards, one permission ask per screen (per Section 4 table), each with icon, one-sentence "why", **"Ruhusu"** (Allow) primary button, small **"Ruka kwa sasa"** (Skip for now) text-link where the permission isn't hard-blocking (e.g. contacts).
- Progress dots at top (e.g. ●●○○).

### 6.4 Onboarding — Weka kama SMS Kuu (Default app)
- Explains why (clean inbox, one app for all SMS), CTA **"Weka Kuu"** launches system default-app-role dialog. If user declines, show a non-blocking retry banner later in Settings.

### 6.5 Onboarding — Nenosiri (Optional app lock)
- Toggle: **"Washa Ulinzi wa PIN/Kidole"** (Enable PIN/fingerprint lock). If enabled, immediately prompt 4–6 digit PIN set + confirm, optional biometric enrollment.
- **"Ruka"** (Skip) available.

### 6.6 Onboarding — Umekamilika
- Confirmation screen, checkmark animation, CTA **"Fungua Messeji"** (Open Messeji) → Home.

### 6.7 Home / Mazungumzo (Conversations tab)
**Top bar:** Left = Messeji wordmark/logo (small). Right = search icon (🔍), overflow menu (⋮) with: Chagua (Select multiple), Hifadhi Zote (Archive all read), Mipangilio shortcut.
**Filter chips row** (horizontally scrollable, sticky under top bar):
`Zote` (All) · `Mtu kwa Mtu` (Person-to-person) · `Pesa na OTP` (Money & OTP) · `Matangazo` (Promotions)
- Auto-categorization logic (client-side, on-device, no data leaves phone):
  - **Pesa na OTP**: sender matches known shortcode patterns/keywords for M-Pesa, Tigo Pesa, Airtel Money, HaloPesa, bank alerts, and numeric OTP patterns (e.g. contains "namba ya uthibitishaji", "OTP", 4–8 digit isolated codes, known bank/MNO sender IDs). Maintain an on-device, updatable keyword/sender list (ship a JSON config bundled with app, updatable via lightweight remote config in Phase 2).
  - **Matangazo**: sender is alphanumeric ad shortcode, message contains promotional trigger words ("punguzo", "ofa", "bei nafuu", "bonyeza link"), or sender not in contacts and message contains a bare URL.
  - **Mtu kwa Mtu**: everything else, default bucket, always includes anyone in the user's contacts regardless of content.
  - User can always manually **"Hamisha kundi"** (Move category) via long-press → context menu, and the system should learn per-sender overrides.
- **Thread row layout:** avatar (contact photo or generated initials on brand-color background) · name or phone number · last message preview (1 line, truncated) · timestamp (relative: "sasa hivi" now, "dakika 5" 5 min, "10:42", "Jana", or date) · unread count badge (gold circle) · pinned icon if pinned · muted icon if muted.
- **Swipe actions:** swipe right = Hifadhi (Archive), swipe left = Futa (Delete, with confirm), long-press = multi-select mode with bulk actions bar (Futa, Hifadhi, Zuia, Bandika/Toa Bandiko, Nyamazisha).
- **Empty state** (no messages yet): friendly illustration, text: **"Hakuna mazungumzo bado. Anza kwa kutuma meseji yako ya kwanza!"** with CTA **"Meseji Mpya"**.
- **Floating action button** (bottom-right, brand green circle, + icon) → New Message composer.

### 6.8 Chat Thread
- **Top bar:** back arrow · contact avatar+name (tap → contact detail) · call icon (dials via phone app) · overflow menu (Zuia, Futa Mazungumzo/Delete conversation, Tafuta Ndani ya Mazungumzo/Search in thread, Nyamazisha).
- **If dual-SIM:** small SIM chip indicator near top showing which SIM this thread will send from, tappable to switch.
- **Message bubbles:** received = left-aligned neutral surface color; sent = right-aligned brand green, white text. Timestamps shown on tap or grouped under time-cluster dividers ("Leo", "Jana", "Juni 3").
- **Delivery/read status** (SMS-realistic, not IM-style): small ticks — sending (clock icon), imetumwa (sent, single check), imefikishwa (delivered, double check where carrier confirms). No fake read-receipts since SMS protocol doesn't support them — do not imply message was read.
- **MMS/attachments:** tap-to-load images (data-saving), inline video thumbnail, file attachments shown as a chip with filename+size+download icon.
- **Composer bar (bottom, sticky):** attachment/plus icon (camera, gallery, contact-share) · text input (expands up to 5 lines then scrolls, placeholder: **"Andika meseji..."**) · character/segment counter appears when approaching SMS 160-char boundary (e.g. "142/160 · SMS 1/2") · send button (paper-plane, brand green, disabled/gray when empty).
- **Long-press a message:** context menu — Nakili (Copy), Peleka Mbele (Forward), Futa (Delete), Taarifa (Info: sent time, delivery status, SIM used).

### 6.9 New Message Composer
- Top: **"Kwa:"** (To:) field with live contact search-as-you-type + "Andika namba ya simu" freeform entry for non-contacts.
- Selecting multiple recipients auto-creates a group MMS thread — warn user: **"Kutuma kwa watu wengi kunaweza kugharimu zaidi"** (Sending to multiple people may cost more).
- Below: same composer bar as thread screen.

### 6.10 Anwani (Contacts tab)
- Alphabetical index list (A–Z fast-scroll rail on right, Swahili alphabet ordering identical to Latin), synced from device contacts (read-only unless user grants write).
- Search bar at top.
- Each row: avatar, name, tap → contact detail (not chat directly, since a contact may have multiple numbers).
- FAB: **"Anwani Mpya"** (New contact) — only if write permission granted; otherwise routes to system Contacts app.

### 6.11 Contact Detail
- Large avatar, name, all phone numbers listed with SIM-type labels if detectable, buttons: **Tuma Meseji** / **Piga Simu** / **Zuia**.
- "Mazungumzo ya Awali" (message history with this contact) — deep-link into thread.

### 6.12 Tafuta (Search) — global
- Accessible from Home top bar. Search-as-you-type across message bodies, contact names, phone numbers. Results grouped under headers: **Anwani** (Contacts) / **Meseji** (Messages, with matched text snippet highlighted and thread context).

### 6.13 Mipangilio (Settings) — top-level list
- **Wasifu** (Profile — device number, display name shown to Send Africa services only if applicable)
- **Arifa** (Notifications) →6.14
- **Muonekano** (Appearance) — Dark/Light/System toggle, bubble color options (future)
- **Kadi za SIM** (SIM cards) — default SIM for sending, per-contact SIM override
- **Namba Zilizozuiwa** (Blocked numbers) → list, unblock action
- **Nakala Rudufu na Kurejesha** (Backup & Restore) →6.15
- **Ulinzi wa Programu** (App lock) — PIN/biometric toggle & change
- **Data na Hifadhi** (Data & Storage) — cache size, auto-download media toggle (Wifi tu / Kila mara / Kamwe — WiFi only / Always / Never), clear cache
- **Unganisha na Kompyuta** (Connect to computer) — Phase 2 entry point, shown as "Inakuja Hivi Karibuni" (Coming soon) badge in MVP or hidden entirely if not built yet
- **Kuhusu** (About) — version, Send Africa branding, privacy policy (in Swahili), terms, licenses
- **Msaada** (Help/Support) — FAQ, contact Send Africa support

### 6.14 Arifa (Notification settings)
- Master toggle: **"Washa Arifa"**
- Per-category toggles: Mtu kwa Mtu, Pesa na OTP, Matangazo (allow users to mute promo notifications entirely — high-value setting given SMS spam is a real pain point in TZ)
- Sauti ya Arifa (notification sound picker), Mtetemo (vibration toggle), Onyesho la Skrini Imefungwa (show preview on lock screen — privacy-sensitive, default ON but easily toggled OFF for the money/OTP category specifically, since OTPs on lock screen are a known fraud vector — actually **default this OFF for Pesa na OTP category** as a security-conscious default, ON for others).

### 6.15 Backup & Restore
- **"Fanya Nakala Rudufu Sasa"** (Backup now) — encrypts SMS DB + contacts refs, saves to local storage or user's Google Drive (if linked).
- Shows last backup date/time, size, toggle for **"Nakala Rudufu Kiotomatiki"** (Auto-backup, e.g. weekly, WiFi-only).
- **"Rejesha kutoka Nakala Rudufu"** (Restore from backup) — file picker, confirm-overwrite warning.
- Encryption note shown to user: **"Nakala yako imesimbwa kwa usalama."** (Your backup is securely encrypted.)

### 6.16 App Lock
- PIN pad (numeric, Swahili numeral labels same as digits — no translation needed), biometric fallback if enrolled.
- Setting: auto-lock timing (Mara moja/Immediately, Dakika 1, Dakika 5, Kamwe/Never).

### 6.17 Blocked Numbers
- List of blocked numbers/senders, each with **"Fungua"** (Unblock) action. Empty state: **"Hakuna namba zilizozuiwa."**

### 6.18 Unganisha na Kompyuta (Phase 2 screen, spec below in Section 8) — build this screen last, after MVP ships and stabilizes.

---

## 7. SYSTEM ARCHITECTURE (MVP, on-device)

### 7.1 Platform & stack
- **Platform:** Native Android (Kotlin), minSdk 24 (covers ~98% of active TZ Android devices), target/compileSdk latest stable.
- **UI:** Jetpack Compose (modern, faster to build consistent design system, better for the animation/theme requirements above).
- **Architecture pattern:** MVVM + unidirectional data flow (Compose State / ViewModel / Repository), Hilt for DI.
- **Local DB:** Room (SQLite) — but note: SMS/MMS content itself lives in the **Android system Telephony provider** (`content://sms`, `content://mms`), which Messeji reads/writes via `Telephony.Sms` and `Telephony.Mms` content resolvers when registered as the default SMS app. Room is used for Messeji-specific metadata layered on top: category tags, pin/mute/archive state, per-sender overrides, blocked list, backup metadata, app settings.
- **Background work:** `BroadcastReceiver` for `SMS_DELIVER`/`WAP_PUSH_DELIVER` intents (only default SMS app receives these), `WorkManager` for scheduled backups and cache cleanup, foreground service only if actively needed for notification reliability on aggressive battery-optimization OEM skins common in the region (Tecno/Infinix/itel HiOS, Xiaomi MIUI) — include a one-time onboarding tip: **"Baadhi ya simu huzuia arifa. Bofya hapa kuruhusu Messeji ifanye kazi vizuri nyuma."** (Some phones block notifications — tap here to allow Messeji to run properly in the background) linking to OEM-specific battery-optimization exemption settings.
- **Notifications:** `NotificationChannel` per category (Mtu kwa Mtu / Pesa na OTP / Matangazo) so users can control importance per-channel at the OS level too; `MessagingStyle` notifications with inline **Jibu** (Reply) and **Soma** (Mark as read) actions; grouped/bundled notifications per-thread when multiple arrive.
- **Security:** SQLCipher or Room+SQLCipher for the Room metadata DB if it ever stores sensitive derived data; Android Keystore for PIN/biometric-gated encryption keys; backups encrypted client-side (AES-256) before writing to storage/Drive — Messeji/Send Africa servers never see message content in MVP since there is no backend requirement for core functionality.
- **No mandatory backend for MVP core feature set** — this is a fully on-device app, which is a deliberate design choice for speed, offline-reliability, low data cost, and privacy trust-building in a market sensitive to money-related SMS fraud.
- **Analytics:** privacy-respecting, aggregate-only, opt-in, no message content ever transmitted (e.g. self-hosted Matomo or PostHog with content-blind event names only: "thread_opened", "backup_completed" — never message text/numbers).

### 7.2 Data model (Room entities, layered on top of system SMS provider)

```
MessageMeta
 - message_id (FK → system SMS/MMS _id + thread reference)
 - category ENUM(person, money_otp, promo)
 - is_manual_override BOOLEAN

ThreadMeta
 - thread_id (FK → system thread_id)
 - is_pinned BOOLEAN
 - is_muted BOOLEAN
 - is_archived BOOLEAN
 - default_sim_slot INT NULLABLE

SenderRule
 - sender_pattern (number or shortcode)
 - forced_category ENUM
 - created_by ENUM(system_default, user_override)

BlockedNumber
 - number
 - blocked_at TIMESTAMP

BackupRecord
 - backup_id
 - created_at
 - size_bytes
 - location ENUM(local, drive)
 - encrypted BOOLEAN

AppSettings
 - key, value (theme, lock_enabled, lock_timeout, notif prefs, data_saver_mode, etc.)
```

### 7.3 Categorization engine
- Bundled JSON ruleset (`category_rules.json`) shipped in `assets/`, containing: known MNO/bank sender ID lists (Vodacom M-Pesa, Tigo Pesa, Airtel Money, Halotel HaloPesa, CRDB, NMB, NBC, etc.), Swahili keyword lists for OTP/promo detection, regex for OTP code patterns.
- Rule evaluation order: (1) contact match → always Mtu kwa Mtu unless user manually overrode, (2) SenderRule table override, (3) bundled ruleset match, (4) fallback heuristics (alphanumeric sender + URL → promo; numeric shortcode + currency/amount pattern → money_otp), (5) default → Mtu kwa Mtu.
- Rules file versioned; Phase 2 can fetch updates from a lightweight config endpoint without an app update.

---

## 8. PHASE 2 SPEC — "UNGANISHA NA KOMPYUTA" (PC Companion via QR Pairing)

Build this after MVP is stable and adopted. Documented in full now so the MVP architecture doesn't block it later.

### 8.1 UX flow
1. User opens **Mipangilio → Unganisha na Kompyuta**.
2. Screen shows: **"Fungua messeji.sendafrica.co (or similar) kwenye kompyuta yako, kisha changanua msimbo huu wa QR."**
3. Phone displays a QR code (regenerates every ~60s for security, like WhatsApp Web).
4. User visits the companion web app on desktop Chrome/Firefox/Edge, which shows a live-updating QR.
5. Phone camera scans QR → pairing handshake completes → desktop shows Messeji inbox mirrored in Swahili.
6. Phone shows a **persistent notification**: "Umeunganishwa na Kompyuta — Bofya kukatisha" (Connected to computer — tap to disconnect), consistent with transparency best practice.
7. Multi-device list in Settings: **"Vifaa Vilivyounganishwa"** (Connected devices) — shows device name, browser, IP-derived rough location, last active, with a **"Katisha"** (Disconnect) action per device and a **"Katisha Vyote"** (Disconnect all) safety action.

### 8.2 Technical architecture
- **Model:** phone-as-relay (same trust model as WhatsApp Web pre-multi-device) — the phone remains the source of truth; desktop is a thin client. This avoids needing message content to ever touch Send Africa servers, preserving the privacy stance from Section 7.1.
- **Pairing handshake:**
  1. Desktop web client generates an ephemeral **Curve25519 keypair**, encodes its public key + a session nonce + a relay-server session ID into the QR payload.
  2. Phone scans QR, generates its own ephemeral keypair, performs an ECDH key exchange to derive a shared session key.
  3. Both sides now share a symmetric key (e.g. via HKDF from the ECDH output) used to encrypt all subsequent relay traffic end-to-end — the relay server only ever sees ciphertext.
- **Transport:** WebSocket connection from both phone and desktop to a lightweight **relay server** (Send Africa infrastructure) that simply forwards encrypted frames by session ID — it cannot decrypt them. This is the one required backend component in the whole product, and it should be architected as a dumb relay, not a data store, to preserve the no-backend-sees-content principle.
- **Session persistence:** after first pairing, store the derived long-term key (phone: Android Keystore-backed; desktop: IndexedDB, non-exportable) so reconnection doesn't require re-scanning every time, similar to multi-device SMS apps' "remember this device" pattern — but always show the phone-side persistent notification whenever a desktop session is actively connected.
- **Sync scope for MVP of Phase 2:** read + send text SMS only from desktop (no MMS/attachments in first cut), notifications mirrored to desktop, typing indicators optional/skippable.
- **Disconnection & security:** any disconnect (manual, timeout, or phone-side revoke) immediately invalidates the session key; a compromised or lost desktop session can be killed instantly from **Vifaa Vilivyounganishwa** on the phone at any time, even if the desktop is unreachable — the phone simply stops accepting frames tied to that session ID.
- **Rate/abuse protection on relay:** basic connection-count and payload-size limits per session; the relay never needs to know the plaintext content it forwards.

### 8.3 Desktop companion UI
- Web app, Swahili-only to match, responsive but desktop-optimized layout: left column = thread list (same categorization chips as mobile), right column = active thread, top bar = connection status + **"Tenganisha"** (Unpair) button.
- Explicitly out of scope for the web app: onboarding, permission flows, settings duplication — it's a mirror, not a parallel app. Settings changes remain phone-only in Phase 2.

---

## 9. NON-FUNCTIONAL REQUIREMENTS

- **Performance target:** cold start < 1.5s on a 2GB RAM device; thread list scroll at 60fps with 500+ conversations via Compose LazyColumn + pagination.
- **APK size target:** under 15MB for the base MVP build (important for low-storage devices and data-cost-conscious sideload/download).
- **Offline-first:** 100% of MVP functionality works with zero internet connectivity, since SMS itself doesn't require data — only backup-to-Drive and Phase 2 desktop sync require connectivity.
- **Battery:** no polling; rely entirely on system broadcast intents for incoming SMS, no unnecessary wake locks.
- **Accessibility:** minimum touch target 48dp, TalkBack labels in Swahili for all interactive elements, color contrast meeting WCAG AA against both light and dark surfaces, scalable text respecting system font-size setting.
- **Localization discipline:** even though this is Swahili-only for MVP, structure all UI strings through Android's standard `strings.xml` resource system (not hardcoded) so a future English or other-language toggle is a resourcing exercise, not a rewrite.
- **Device coverage:** test matrix should include at minimum Tecno, Infinix, itel (HiOS/XOS heavy customization + aggressive battery killers), Samsung (common mid-range), and a stock/AOSP-like reference device.

---

## 10. SUCCESS METRICS (MVP)

- % of installs that complete the default-SMS-app grant (primary activation metric)
- D1/D7/D30 retention
- Average categorization accuracy (sampled user-correction rate via manual override frequency — proxy since content can't be analyzed centrally)
- Spam-block actions per active user per week (proxy for the pain-point this app solves)
- Crash-free session rate (target >99.5%, critical given SMS is trust-sensitive infrastructure)
- Backup adoption rate

---

## 11. BRANDING FOOTER (required on About screen)

> Messeji imetengenezwa na **Send Africa**. © [year]. Faragha yako ni muhimu kwetu — meseji zako hazitumwi kwenye seva zetu.
> (Messeji is built by Send Africa. Your privacy matters to us — your messages are never sent to our servers.)

---

## 12. BUILD ORDER RECOMMENDATION

1. Data layer: Telephony provider read/write wrapper + Room metadata DB + categorization engine
2. Onboarding + permissions flow
3. Home/Mazungumzo thread list (read-only first, then send capability)
4. Chat thread screen with send/receive + notifications
5. Contacts tab + New Message composer
6. Search
7. Settings shell + all sub-screens (notifications, SIM, blocked, appearance)
8. App lock
9. Backup & Restore
10. Polish pass: dark mode, empty states, error states, OEM battery-optimization onboarding tip
11. QA across device matrix, then release
12. Phase 2: relay server + QR pairing + desktop web client

---

*End of specification.*
