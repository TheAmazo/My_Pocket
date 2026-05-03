# Firebase Setup Guide

Use this guide with the Firebase project `my-pocket-a381c` and Android package `com.thisara.mypocket`.

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
2. Click **Get started** if needed.
3. Enable **Email/Password**.
4. Enable **Google**.
5. Set a public support email for Google sign-in.

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
2. Create a database.
3. Start in production mode.
4. Choose the closest available region.
5. Open **Rules** and paste the contents of `firestore.rules`.
6. Publish the rules.

## 4. First Run Checklist

1. Run the app from Android Studio.
2. Create an account with email and password.
3. Open the verification email and verify the account.
4. Log in and create a pocket.
5. Copy the invite code from Settings.
6. On another account, join using that invite code.
7. Mark a savings cell and confirm the total updates on both accounts.

## 5. Common Problems

- **Google sign-in says it is not ready:** add SHA fingerprints, then download a fresh `google-services.json`.
- **Firestore permission denied:** publish `firestore.rules` in Firebase Console.
- **GitHub Desktop shows too many files:** check that `.gitignore` is present at the repo root.
