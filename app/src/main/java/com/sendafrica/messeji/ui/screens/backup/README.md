# Backup & Restore Screen (`ui/screens/backup/`)

## `BackupRestoreScreen.kt`

Manage encrypted backups of SMS and metadata. Built by Camel Creatives.

```
┌─────────────────────────────────────┐
│ TopAppBar: Nakala Rudufu...  [←]    │
├─────────────────────────────────────┤
│ ┌─────────────────────────────────┐ │
│ │ 💾 Nakala yako imesimbwa kwa   │ │
│ │    usalama.                     │ │
│ └─────────────────────────────────┘ │
│                                     │
│ ┌──────────────────────────────┐   │
│ │ 💾 Fanya Nakala Rudufu Sasa  │   │
│ └──────────────────────────────┘   │
│                                     │
│ Nakala ya mwisho: Januari 15, 2024  │
│             14:30                   │
│                                     │
│ Nakala Rudufu Kiotomatiki [🔘]     │
│                                     │
│ ┌──────────────────────────────┐   │
│ │ 🔄 Rejesha kutoka Nakala     │   │
│ │    Rudufu                     │   │
│ └──────────────────────────────┘   │
└─────────────────────────────────────┘
```

### Implementation Notes

- **Encryption note** displayed in a Card: "Nakala yako imesimbwa kwa usalama."
- **Backup Now** button triggers `MessageRepository.createBackup()` and stores timestamp
- **Last backup** timestamp shown if one exists (formatted with Swahili locale)
- **Auto-backup toggle** enables/disables scheduled backups (future: WorkManager periodic task)
- **Restore button** opens a file picker (MVP: placeholder, actual file picker + restore logic to be implemented)
- Encryption: AES-256 before writing to storage (MVP: metadata record, full encryption in next iteration)
