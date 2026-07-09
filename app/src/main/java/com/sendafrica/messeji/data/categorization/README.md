# Categorization Engine (`data/categorization/`)

The categorization engine is the app's "smart inbox" brain. It runs **entirely on-device** — no message data ever leaves the phone.

## Core Class: `CategoryEngine.kt`

### Message Categories

| Enum | Value | Meaning |
|------|-------|---------|
| `PERSON` | "person" | Person-to-person messages (Mtu kwa Mtu) |
| `MONEY_OTP` | "money_otp" | Mobile money alerts, bank messages, verification codes (Pesa na OTP) |
| `PROMO` | "promo" | Promotional messages, spam, advertisements (Matangazo) |

### Rule Evaluation Order

```
1. Is sender in device contacts? ──→ PERSON (unless user override exists)
2. Is there a user override in SenderRule table? ──→ Use that category
3. Check bundled ruleset (assets/category_rules.json)
   ├── Exact sender match
   ├── Sender contains match
   ├── Sender starts-with match
   ├── Keyword match in message body
   └── Regex pattern match
4. Fallback heuristics
   ├── Alphanumeric sender + bare URL → PROMO
   ├── Amount pattern (TSh/TSH + digits) → MONEY_OTP
   ├── OTP keywords present → MONEY_OTP
   └── Known money sender IDs → MONEY_OTP
5. Default → PERSON
```

### Bundled Rules (`assets/category_rules.json`)

Shipped with the APK and loaded on first launch. Contains:
- **Known MNO/bank sender IDs**: M-PESA, TIGOPESA, AIRTELMONEY, HALOPESA, CRDB, NMB, EQUITY, KCB, etc.
- **OTP detection keywords**: "namba ya uthibitishaji", "msimbo wa uthibitisho", "OTP", "verification code", "uthibitisho"
- **Money transaction keywords**: "umepokea", "umetuma", "salio"
- **Promo keywords**: "punguzo", "ofa maalum", "bei nafuu", "bonyeza link", "zawadi", "bonus", "tangazo"
- **Regex patterns**: TSh amounts, numeric OTP codes, URLs

The rules file is versioned (top-level `"version"` field) and can be updated via lightweight remote config without an app update in Phase 2.

### User Overrides
When a user long-presses a thread and selects "Hamisha kundi" (Move category), a `SenderRule` with `createdBy = "user_override"` is inserted. The engine checks this table before the bundled ruleset, giving user preferences priority.
