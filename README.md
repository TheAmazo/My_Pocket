# My Pocket

My Pocket is a native Android savings app for couples who want a simple, shared way to save money without messy paper sheets, forgotten marks, or wrong monthly totals.

The app turns a random savings sheet into a live monthly board. Each pocket has tappable saving cells, automatic totals, email and Google login, Firestore syncing, daily reminders, monthly summaries, yearly summaries, and support for multiple shared pockets.

## Highlights

- Shared couple savings pockets with invite codes
- Multiple pockets per user
- Email/password signup with email verification
- Google sign-in support
- Firebase Firestore live syncing
- 30-cell random monthly savings boards
- Sheet-style denominations: `20`, `50`, `100`, `500`, `1000`
- Today date shown on the active board
- Cells saved on previous days become locked and gray
- Saved cells move lower so today's available choices stay easy to scan
- New random cells generate if the month is completed early
- Monthly saved and missed amount summary
- Yearly saved and missed amount summary
- Daily reminder when no saving was marked
- Responsive Jetpack Compose UI for phones and larger Android screens

## App Flow

```text
Landing
  -> Sign up or log in
  -> Verify email
  -> Create or join a pocket
  -> Use the monthly savings board
  -> Review monthly and yearly summaries
  -> Switch or create more pockets
```

## Screens

The app includes these main screens:

- Landing screen
- Sign up screen
- Login screen
- Email verification screen
- Pocket selector
- Create pocket and join pocket flow
- Monthly savings board
- Summary tab
- Settings tab

## Savings Rules

My Pocket is designed around practical real-world behavior:

- You can mark any number of cells on the current day.
- A cell saved today can still be corrected today.
- After the day passes, that saved cell becomes locked.
- Locked cells are shown in gray and sorted lower on the board.
- Unsaved cells stay open until they are saved.
- If all active cells are saved before the month ends, the app adds a fresh random 30-cell round.
- At month end, the summary shows how much was saved and how much was not saved.

## Tech Stack

| Area | Technology |
| --- | --- |
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Architecture | ViewModel, StateFlow, repository layer |
| Authentication | Firebase Auth |
| Database | Cloud Firestore |
| Local settings | Android DataStore |
| Reminders | Android AlarmManager and notifications |
| Build | Gradle Kotlin DSL |
| Tests | JUnit |

## Project Structure

```text
My_Pocket/
|-- app/
|   |-- src/main/java/com/thisara/mypocket/
|   |   |-- data/
|   |   |   |-- FirebaseRepository.kt
|   |   |   |-- Models.kt
|   |   |   |-- ReminderPolicy.kt
|   |   |   |-- SavingsBoardGenerator.kt
|   |   |   `-- SettingsStore.kt
|   |   |-- reminders/
|   |   |   |-- BootReceiver.kt
|   |   |   |-- ReminderReceiver.kt
|   |   |   `-- ReminderScheduler.kt
|   |   |-- ui/
|   |   |   |-- MainViewModel.kt
|   |   |   |-- MyPocketApp.kt
|   |   |   `-- theme/
|   |   |       `-- Theme.kt
|   |   |-- MainActivity.kt
|   |   `-- MyPocketApplication.kt
|   |-- src/main/res/
|   `-- google-services.json
|-- app/src/test/
|-- docs/
|   `-- FIREBASE_SETUP.md
|-- firestore.rules
|-- firebase.json
|-- gradle/
|-- build.gradle.kts
`-- settings.gradle.kts
```

`google-services.json` is required locally, but it is ignored by Git.

## Firebase Setup

Use the Firebase project:

```text
my-pocket-a381c
```

Android package:

```text
com.thisara.mypocket
```

Required Firebase products:

- Authentication
- Firestore Database

Required Auth providers:

- Email/Password
- Google

For the full setup guide, see:

```text
docs/FIREBASE_SETUP.md
```

Important: publish `firestore.rules` in Firebase Console after changing rules.

## Run Locally

Open the root folder in Android Studio:

```text
/Users/thisara/Desktop/My_Pocket
```

Do not open only the `app` folder.

Then:

1. Wait for Gradle Sync.
2. Select the `app` run configuration.
3. Select an emulator or Android phone.
4. Press Run.

You can also build from Terminal:

```bash
./gradlew test
./gradlew lintDebug
./gradlew assembleDebug
```

The debug APK is generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Git Notes

The repo is configured so GitHub Desktop shows real project changes while ignoring local/generated files:

- `.idea/`
- `.gradle/`
- `local.properties`
- `app/build/`
- `app/google-services.json`

## Current Status

Implemented:

- Android Studio project setup
- Firebase Auth integration
- Firestore repository layer
- Shared pocket creation and invite-code joining
- Multi-pocket selector
- Monthly savings board
- Locked past-day cells
- Auto-generation of new cells when the board fills early
- Monthly and yearly summaries
- Daily reminders
- Unit tests for board generation and locking rules

Verified commands:

```bash
./gradlew test assembleDebug
./gradlew lintDebug
```

## Roadmap

Possible next improvements:

- Add charts for monthly and yearly savings
- Add custom monthly target mode
- Add export to PDF or CSV
- Add partner activity feed
- Add dark theme
- Add biometric app lock
- Add currency selector

## License

Private learning project.
