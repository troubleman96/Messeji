# Lock Screens (`ui/screens/lock/`)

App lock functionality: PIN entry and setup. Built by Camel Creatives.

## `LockScreen.kt`

The lock screen shown when the app is opened and app lock is enabled.

```
┌─────────────────────────────────────┐
│                                     │
│           Messeji                   │
│       Piga nenosiri lako            │
│                                     │
│       ┌──────────────────┐          │
│       | ● ● ● ● ● ●      |          │
│       └──────────────────┘          │
│                                     │
│       ┌─────────────────────┐       │
│       │     Fungua          │       │
│       └─────────────────────┘       │
└─────────────────────────────────────┘
```

- PIN entry with `PasswordVisualTransformation`
- Numeric keyboard input type
- Error state with "Nenosiri si sahihi" message
- On successful PIN match, calls `MainViewModel.unlock()` and navigates to Home

## `LockSetupScreen.kt`

Setup screen accessible from Settings → Ulinzi wa Programu.

### Features:
- **Toggle**: Enable/disable app lock
- **PIN setup**: 4–6 digit PIN entry with confirmation
- **Validation**: PIN length check (4–6 digits), match confirmation
- **Error states**: "Nambari hazilingani", "Nenosiri lazima liwe tarakimu 4 hadi 6"
- **Biometric toggle**: "Tumia kidole gusa / uso" (fingerprint/face)
- **Auto-lock timing**: Mara moja (Immediate), Dakika 1, Dakika 5, Kamwe (Never)
- Radio button selection for timeout values
- Save button persists all settings via `AppSettings`
