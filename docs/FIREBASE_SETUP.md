# Firebase Setup Guide

Use this guide with Firebase project `my-pocket-a381c` and Android package `com.thisara.mypocket`.

## 1. Android App Config

1. In Firebase Console, open **Project Overview**.
2. Confirm the Android app package is `com.thisara.mypocket`.
3. Download `google-services.json`.
4. Place it here:

```text
/Users/thisara/Desktop/My_Pocket/app/google-services.json
```

The file is intentionally ignored by Git.

## 2. Authentication

1. Open **Authentication**.
2. Enable **Email/Password**.
3. Enable **Google**.
4. Set a public support email for Google sign-in.

For Google sign-in, add SHA fingerprints:

```bash
./gradlew signingReport
```

Copy the debug `SHA-1` and `SHA-256` values into Firebase:

```text
Project settings > Your apps > Android app > Add fingerprint
```

After adding fingerprints, download a fresh `google-services.json` and replace the local file in `app/`.

## 3. Firestore Database

1. Open **Firestore Database**.
2. Create a database if it does not exist.
3. Start in production mode.
4. Open **Rules**.
5. Paste the contents of `firestore.rules`.
6. Publish the rules.

## 4. Profile Photos

Profile photos use the free-plan approach:

```text
users/{uid}.photoData
```

No Firebase Storage setup is required.

## 5. Security Checklist

1. Keep `app/google-services.json` local and out of Git.
2. Publish the exact `firestore.rules` file before testing real accounts.
3. Keep Firestore in production mode.
4. Enable only the auth providers you use: Email/Password and Google.
5. Add SHA-1 and SHA-256 fingerprints for every debug/release build you test.
6. Do not store signing keys, passwords, or Firebase service-account files in the repo.
7. For a public release, create a real release keystore and keep it outside Git.

## 6. First Run Checklist

1. Run the app from Android Studio.
2. Create an account with email and password.
3. Open the verification email and verify the account.
4. Log in and create a pocket.
5. Mark multiple savings cells and confirm totals update.
6. Open Summary and confirm monthly history appears.
7. Open Profile and test editing the name and avatar.

## 7. Common Problems

- **Android Studio says there is no Gradle build:** open `/Users/thisara/Desktop/My_Pocket`, not a moved or Expo folder.
- **Google sign-in says it is not ready:** add SHA fingerprints, then download a fresh `google-services.json`.
- **Firestore permission denied:** publish `firestore.rules` in Firebase Console.
- **Avatar save fails:** publish `firestore.rules`; Firebase Storage is not used.
- **GitHub Desktop shows too many files:** confirm `.gitignore` is present at the repo root.
