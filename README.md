<div align="center">

  <img src="assets/my-pocket-icon.svg" width="112" alt="My Pocket icon" />

</div>

# My Pocket

<div align="center">

<pre>
███╗   ███╗██╗   ██╗    ██████╗  ██████╗  ██████╗██╗  ██╗███████╗████████╗
████╗ ████║╚██╗ ██╔╝    ██╔══██╗██╔═══██╗██╔════╝██║ ██╔╝██╔════╝╚══██╔══╝
██╔████╔██║ ╚████╔╝     ██████╔╝██║   ██║██║     █████╔╝ █████╗     ██║
██║╚██╔╝██║  ╚██╔╝      ██╔═══╝ ██║   ██║██║     ██╔═██╗ ██╔══╝     ██║
██║ ╚═╝ ██║   ██║       ██║     ╚██████╔╝╚██████╗██║  ██╗███████╗   ██║
╚═╝     ╚═╝   ╚═╝       ╚═╝      ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝   ╚═╝
</pre>

### A glassy personal savings board for monthly goals, pocket purpose, and calm daily tracking

Black glass UI • Clean monthly targets • Firebase sync • Smart reminders

![Release](https://img.shields.io/github/v/release/TheAmazo/My_Pocket?style=for-the-badge&logo=github&logoColor=white&labelColor=18181B&color=03AED2)
![Downloads](https://img.shields.io/github/downloads/TheAmazo/My_Pocket/total?style=for-the-badge&logo=github&logoColor=white&labelColor=18181B&color=F45B26)
![Stars](https://img.shields.io/github/stars/TheAmazo/My_Pocket?style=for-the-badge&logo=github&logoColor=white&labelColor=18181B&color=F8DE22)

<br>

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=111827)
![Material 3](https://img.shields.io/badge/Material%203-D12052?style=for-the-badge&logo=materialdesign&logoColor=white)

</div>

---

## Download

<div align="center">

<h3>Latest Release</h3>

<a href="https://github.com/TheAmazo/My_Pocket/releases/latest">
  <img src="https://img.shields.io/badge/Get%20My%20Pocket-GitHub%20Release-03AED2?style=for-the-badge&logo=github&logoColor=white" alt="Get My Pocket on GitHub" />
</a>

<br><br>

<a href="https://github.com/TheAmazo/My_Pocket/releases/download/v3.0.1/MyPocket-v3.0.1-debug.apk">
  <img src="https://img.shields.io/badge/Download%20APK-v3.0.1-F45B26?style=for-the-badge&logo=android&logoColor=white" alt="Download My Pocket v3.0.1 APK" />
</a>

</div>

### Version 3.0.1 APK

The version 3 GitHub release includes a test-installable APK:

```text
MyPocket-v3.0.1-debug.apk
```

Download it from [My Pocket v3.0.1](https://github.com/TheAmazo/My_Pocket/releases/tag/v3.0.1).

SHA-256:

```text
97709846449e30e798a6e86bdfa85b20b9c5c32960870d4496842a5c2d8e375f
```

This APK is debug-signed for version 3 testing. A production APK should be signed with a private release keystore that stays outside Git.

### What Is New In v3.0.1

- Open savings cards now get fresh lower-biased random values once per day, while saved and locked history stays unchanged.
- Android system back now follows in-app navigation on supported screens.
- Reminder time now opens a clock picker and requests notification permission when needed.
- A refined iOS-inspired black glass dark theme with white-blend glass cards.
- A unified wallet logo across launcher, onboarding, pocket picker, and README.
- Pocket creation now captures both pocket name and purpose.
- Pocket headers now show the selected pocket name instead of a repeated app title.
- Summary is cleaner with one yearly saved card above monthly history.
- Daily reminders support both hour and minute settings.
- Theme settings now support System follow, Light, and Dark.
- Google sign-in error 10 now explains the Firebase SHA fingerprint fix clearly.

---

## Why My Pocket?

My Pocket replaces manual savings sheets with a focused Android app. Each pocket can have its own purpose, every month gets a clean savings board, and the app keeps saved totals, remaining amounts, yearly progress, and reminders in sync without messy marks or wrong calculations.

---

## Preview

### Light Card Mode

<div align="center">

<img src="Screenshots/light/onboard.png" width="170" alt="My Pocket light landing screen" />
<img src="Screenshots/light/signup.png" width="170" alt="My Pocket light signup screen" />
<img src="Screenshots/light/login.png" width="170" alt="My Pocket light login screen" />
<img src="Screenshots/light/pockets.png" width="170" alt="My Pocket light pocket selector screen" />

<br><br>

<img src="Screenshots/light/board.png" width="170" alt="My Pocket light board screen" />
<img src="Screenshots/light/summary.png" width="170" alt="My Pocket light summary screen" />
<img src="Screenshots/light/settings.png" width="170" alt="My Pocket light settings screen" />
<img src="Screenshots/light/profile.png" width="170" alt="My Pocket light profile screen" />

</div>

### iOS-Inspired Black Glass Mode

<div align="center">

<img src="Screenshots/dark/onboard.png" width="170" alt="My Pocket dark landing screen" />
<img src="Screenshots/dark/signup.png" width="170" alt="My Pocket dark signup screen" />
<img src="Screenshots/dark/login.png" width="170" alt="My Pocket dark login screen" />

<br><br>

<img src="Screenshots/dark/pockets.png" width="170" alt="My Pocket dark pocket selector screen" />
<img src="Screenshots/dark/settings.png" width="170" alt="My Pocket dark settings screen" />
<img src="Screenshots/dark/profile.png" width="170" alt="My Pocket dark profile screen" />

</div>

---

## Highlights

### Version 3 Experience

- Premium light card mode for clean daily use
- iOS 26-inspired black glass dark mode with bright contrast
- Rounded finance-style cards, buttons, inputs, and board cells
- Theme modes for System follow, Light, and Dark
- Unified wallet logo across launcher, onboarding, pocket picker, and README
- Password show and hide controls on password fields

### Savings Board

- 30 monthly cells with daily open-card values from `20`, `50`, `100`, `500`, and `1000`
- Multiple cells can be saved on the same day
- Open cells stay at the top so saving feels natural
- Saved cells from previous days lock and move below open cells
- If a monthly board is completed early, a new random round is generated
- Monthly target, saved total, remaining total, and progress update automatically

### Pockets

- Create multiple personal pockets with a custom purpose
- Edit pocket name and purpose safely
- Delete pockets only after confirmation
- Duplicate pocket names are blocked
- Deleting one pocket does not damage the rest of the account

### Summary

- Yearly overview with 12 month cards
- Single Yearly saved card above monthly summaries
- Saved total, missed amount, completed cells, and progress per month
- Month detail screen shows every day of the month
- Saved rows include amount, saved time, and saved user name

### Profile

- Editable full name
- Email shown as read-only account identity
- Account created date and last login date
- Change password flow for email accounts
- Delete account flow with confirmation
- Free Firebase Spark-plan avatar storage using compressed Firestore `photoData`

### Security And Reliability

- Firebase Auth email verification
- Google sign-in support
- Owner-only Firestore rules
- Firestore validation for users, pockets, months, and cells
- Clear Google status code 10 guidance for missing Firebase SHA fingerprints
- No Firebase Storage required for avatars
- Cleartext HTTP disabled
- Android backup disabled for app data
- Release build uses R8 shrinking and obfuscation
- Local Firebase config and signing files are ignored by Git

---

## Tech Stack

| Layer | Stack |
| --- | --- |
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | ViewModel + StateFlow + repository layer |
| Authentication | Firebase Auth |
| Database | Cloud Firestore |
| Local Settings | Android DataStore |
| Reminders | AlarmManager + Notifications |
| Build | Gradle Kotlin DSL |
| Tests | JUnit |

---

## Project Structure

```text
My_Pocket/
├── app/
│   ├── src/main/java/com/thisara/mypocket/
│   │   ├── data/
│   │   ├── reminders/
│   │   ├── ui/
│   │   ├── MainActivity.kt
│   │   └── MyPocketApplication.kt
│   ├── src/main/res/
│   └── google-services.json        # local only, ignored by Git
├── assets/
│   └── my-pocket-icon.svg
├── docs/
│   └── FIREBASE_SETUP.md
├── Screenshots/
│   ├── dark/
│   └── light/
├── firestore.rules
├── firebase.json
├── gradle/
├── build.gradle.kts
└── settings.gradle.kts
```

---

## Run Locally

### Requirements

- Android Studio Ladybug or newer
- JDK 17
- Android SDK 36
- Firebase project `my-pocket-a381c`
- Local `app/google-services.json`

### Android Studio

Open the full project folder:

```text
/Users/thisara/Desktop/My_Pocket
```

Then:

1. Wait for Gradle Sync.
2. Select the `app` run configuration.
3. Select an emulator or Android phone.
4. Press Run.

Do not open only the `app` folder. Android Studio needs the root Gradle files.

### Terminal Checks

```bash
./gradlew test assembleDebug
./gradlew lintDebug
./gradlew assembleRelease
```

---

## Firebase Setup

Firebase project:

```text
my-pocket-a381c
```

Android package:

```text
com.thisara.mypocket
```

Required products:

- Authentication
- Cloud Firestore

Not required:

- Firebase Storage
- Cloud Functions
- Paid Firebase plan

Full setup guide: [docs/FIREBASE_SETUP.md](docs/FIREBASE_SETUP.md)

---

## Firestore Shape

```text
users/{uid}
pockets/{pocketId}
pockets/{pocketId}/months/{monthKey}
pockets/{pocketId}/months/{monthKey}/cells/{cellId}
```

Pocket documents include the owner, display name, and purpose:

```text
pockets/{pocketId}.name
pockets/{pocketId}.purpose
pockets/{pocketId}.createdBy
```

Profile avatars are stored as small compressed Base64 data strings:

```text
users/{uid}.photoData
```

This keeps the app compatible with Firebase Spark plan.

---

## Version 3 Checklist

- Email/password signup and verification
- Google sign-in
- Create, edit, delete, and switch pockets
- Duplicate pocket-name blocking
- Monthly savings board
- Multiple saves per day
- Locked previous-day cells
- Yearly and monthly summary
- Profile edit, avatar, password, and account delete tools
- Daily reminders with hour and minute settings
- Responsive UI for phones, tablets, portrait, and landscape
- Firestore rules validated with the local emulator

---

## Git Notes

GitHub Desktop should show source changes without IDE/build noise. These files stay local:

- `.idea/`
- `.gradle/`
- `.kotlin/`
- `local.properties`
- `app/build/`
- `app/google-services.json`
- signing keys
- Firebase debug logs

---

## License

Private learning project.
