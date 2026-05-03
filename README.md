# My Pocket

My Pocket is a native Android app for shared couple savings. It replaces a manual savings sheet with a monthly tappable board, automatic totals, daily reminders, Firebase login, and shared Firestore syncing.

## Project Structure

```text
My_Pocket/
├── app/
│   ├── src/main/java/com/thisara/mypocket/
│   │   ├── data/          # Firebase, models, board generation, local settings
│   │   ├── reminders/     # Daily notification scheduler and receivers
│   │   ├── ui/            # Compose screens and view model
│   │   └── ui/theme/      # Palette and Material theme
│   ├── src/main/res/      # App icon, theme, strings, backup rules
│   └── google-services.json  # Local Firebase config, ignored by Git
├── app/src/test/          # Unit tests for board and reminder logic
├── docs/                  # Firebase setup guide
├── firestore.rules        # Firestore security rules
└── gradle/                # Gradle wrapper and version catalog
```

## Local Build

```bash
./gradlew test
./gradlew assembleDebug
```

Open this folder in Android Studio to run the app on an emulator or Android phone.
