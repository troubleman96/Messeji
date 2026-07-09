# Resources (`res/`)

Android resource files for Messeji. All user-facing text is in Swahili only.

## Structure

| Directory | Contents |
|-----------|----------|
| `values/` | Colors, strings (Swahili), light theme |
| `values-night/` | Dark theme override |
| `xml/` | Network security config, file provider paths |
| `drawable/` | Vector drawables, icons |
| `raw/` | Raw assets (future: notification sounds) |
| `mipmap-*/` | App launcher icons |

## Key Files

### `values/strings.xml`
**The most important resource file.** Contains all user-facing text in Swahili. Organized by feature area:

| Section | Prefix | Example |
|---------|--------|---------|
| General | (none) | `karibu`, `anza`, `ruhusu`, `futa`, `hifadhi` |
| Navigation | (none) | `mazungumzo`, `anwani`, `mipangilio` |
| Categories | (none) | `mtu_kwa_mtu`, `pesa_na_otp`, `matangazo` |
| Chat | (none) | `andika_meseji`, `imetumwa`, `imefikishwa` |
| Permissions | `perm_` | `perm_sms_body`, `perm_contacts_title` |
| Onboarding | `onboarding_` | `weka_sms_kuu`, `umekamilika` |
| Settings | (none) | `arifa`, `muonekano`, `kadi_za_sim` |
| Time | (none) | `sasa_hivi`, `jana`, `leo` |
| About | (none) | `messeji_footer` |

### `colors.xml`
Brand color palette defined as Android color resources for XML layouts and theme references.

### `themes.xml`
- **Light**: `Theme.Messeji` extends `android:Theme.Material.Light.NoActionBar`
- **Dark**: `Theme.Messeji` extends `android:Theme.Material.NoActionBar`
- **Splash**: `Theme.Messeji.Splash` with `windowBackground` set to primary green

### `xml/network_security_config.xml`
Disables cleartext traffic, trusts only system certificates. Important for a messaging app handling sensitive OTP content.

### `xml/file_paths.xml`
FileProvider paths for attachment sharing (camera, gallery, files).
