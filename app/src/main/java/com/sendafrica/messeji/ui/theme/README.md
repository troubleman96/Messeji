# Theme (`ui/theme/`)

Material 3 theme system with light and dark color schemes.

## Files

### `Color.kt`
Defines the Messeji brand color palette.

| Color | Hex | Usage |
|-------|-----|-------|
| `Primary` | `#0B6E4F` | Deep Kilimanjaro green — brand color, CTAs, sent bubbles |
| `PrimaryDark` | `#08573E` | Status bar, dark variants |
| `PrimaryLight` | `#0E8F67` | Hover/press states |
| `Accent` | `#F2A93B` | Sun-gold — unread badges, money/OTP tags |
| `Alert` | `#C1272D` | Maasai red — delete, block, errors |
| `SurfaceLight` | `#F7F7F5` | Light mode background |
| `CardLight` | `#FFFFFF` | Light mode cards |
| `SurfaceDark` | `#0F1512` | Dark mode background |
| `CardDark` | `#161D19` | Dark mode cards |

### `Type.kt`
Typography scale using Material 3 defaults with adjusted sizes:
- Headers: 20–24sp Semibold
- Body: 15–16sp Regular
- Smallest: 13sp (accessibility minimum)
- Labels: 11–14sp

### `Theme.kt`
Provides `MessejiTheme` composable with:
- `LightColorScheme` and `DarkColorScheme` using Material 3 `lightColorScheme()`/`darkColorScheme()`
- Automatic status bar color management via `SideEffect`
- System dark theme detection (`isSystemInDarkTheme()`)
- Theme mode override from `AppSettings.themeMode` ("light", "dark", "system")
