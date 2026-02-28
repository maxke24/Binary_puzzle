# Binary Puzzle — Android App

A Jetpack Compose Android app for solving binary (Takuzu) puzzles with Google Sign-In and Firebase progress tracking.

## Features

- **Three difficulty levels**: Easy (6×6), Medium (8×8), Hard (10×10)
- **Procedurally generated puzzles** — a new unique puzzle every time
- **Real-time validation** — invalid cells are highlighted as you play
- **Live timer** per puzzle
- **Google Sign-In** for authentication
- **Firebase Firestore** to persist your completed puzzles and best times across devices
- **Progress screen** with stats by difficulty and personal best times
- Material Design 3 with dynamic colour (Android 12+)

## How to Play

Fill the grid with `0`s and `1`s following these rules:

1. Each row and column must contain an **equal number** of `0`s and `1`s
2. **No three** consecutive identical digits in any row or column
3. All rows must be **unique**, and all columns must be **unique**

Tap an empty cell to place a `0`, tap again for `1`, tap once more to clear it.

---

## Setup Instructions

### 1. Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- An Android device or emulator running API 24+

### 2. Firebase Project Setup

You **must** create a Firebase project and add `google-services.json` before building.

#### Step 1 — Create a Firebase project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click **Add project** and follow the wizard

#### Step 2 — Register the Android app

1. In Firebase Console → Project Overview → **Add app** → Android
2. Use package name: `com.binarypuzzle.app`
3. Enter your app's **SHA-1 fingerprint** (required for Google Sign-In):
   ```bash
   # Debug keystore (for development)
   keytool -list -v -alias androiddebugkey \
     -keystore ~/.android/debug.keystore \
     -storepass android -keypass android
   ```
4. Download **`google-services.json`** and place it at: `app/google-services.json`

#### Step 3 — Enable Firebase services

In Firebase Console:

- **Authentication** → Sign-in method → Enable **Google**
- **Firestore Database** → Create database (start in test mode for development)

#### Step 4 — Firestore Security Rules (production)

Replace test-mode rules with:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /progress/{docId} {
      allow read, write: if request.auth != null
        && request.auth.uid == resource.data.userId;
      allow create: if request.auth != null
        && request.auth.uid == request.resource.data.userId;
    }
  }
}
```

### 3. Build and Run

```bash
# Clone the repo
git clone <repo-url>
cd Binary_puzzle

# Place your google-services.json in app/
cp /path/to/google-services.json app/

# Open in Android Studio, or build from command line:
./gradlew assembleDebug
```

> **Note:** The `gradle-wrapper.jar` is not committed. Android Studio will download it automatically when you open the project, or run:
> ```bash
> gradle wrapper --gradle-version 8.6
> ```

---

## Project Structure

```
app/src/main/java/com/binarypuzzle/app/
├── auth/
│   └── GoogleAuthManager.kt      # Firebase + Google Sign-In logic
├── data/
│   ├── model/UserProgress.kt     # Firestore data model
│   └── repository/ProgressRepository.kt
├── game/
│   ├── BinaryPuzzle.kt           # Puzzle data class & difficulty enum
│   ├── PuzzleGenerator.kt        # Backtracking puzzle generator
│   └── PuzzleValidator.kt        # Rule validation, win detection
├── navigation/AppNavigation.kt   # Compose NavHost
├── ui/
│   ├── screens/
│   │   ├── LoginScreen.kt
│   │   ├── HomeScreen.kt
│   │   ├── GameScreen.kt
│   │   └── ProgressScreen.kt
│   └── theme/                    # Material3 colour, type, theme
├── viewmodel/
│   ├── AuthViewModel.kt
│   ├── GameViewModel.kt
│   └── ProgressViewModel.kt
├── BinaryPuzzleApplication.kt
├── MainActivity.kt
└── ViewModelFactories.kt
```

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Navigation | Navigation Compose |
| State | ViewModel + StateFlow |
| Auth | Firebase Auth (Google Sign-In) |
| Database | Firebase Firestore |
| Async | Kotlin Coroutines |
| Build | Gradle 8.6 / AGP 8.2.2 |
| Min SDK | 24 (Android 7.0) |
