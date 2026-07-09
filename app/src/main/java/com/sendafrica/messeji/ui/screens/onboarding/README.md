# Onboarding Screen (`ui/screens/onboarding/`)

## `OnboardingScreen.kt`

6-step first-run flow that guides the user through permissions and setup. Built by Camel Creatives.

### Steps

| Step | Content | Skippable? |
|------|---------|------------|
| 0 | **Karibu** — Welcome with "Anza" button | No |
| 1 | **Ruhusa ya Meseji** — SMS permission request | No (hard requirement) |
| 2 | **Weka kama SMS Kuu** — Set as default SMS app via `RoleManager` | Yes |
| 3 | **Ruhusa ya Anwani** — Contacts permission | Yes |
| 4 | **Ruhusa ya Arifa** — Notification permission | Yes |
| 5 | **Ulinzi wa Programu** — Optional PIN/biometric lock setup | Yes |
| 6 | **Umekamilika** — Completion with "Fungua Messeji" | No |

### Implementation Details

- **Step transitions** use `AnimatedContent` for smooth cross-fade animation
- **Progress dots** at top show current step (● ● ○ ○ ○ ○ ○)
- **Permission requests** use the standard Android `requestPermissions()` flow
- **Default SMS app** uses `RoleManager.createRequestRoleIntent(ROLE_SMS)` on Android 10+, falls back to `Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS` on older versions
- **App lock** settings are persisted immediately via `AppSettings`
- **Completion** sets `onboardingComplete = true` in DataStore, which routes to Home on subsequent launches
