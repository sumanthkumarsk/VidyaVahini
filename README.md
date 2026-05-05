# Vidya-Vahini: Rural Bus Tracking System

Vidya-Vahini is a real-time bus tracking application designed for students in rural areas. It features crowdsourced location pings, breakdown alerts, and parent notification systems.

---

## 🚀 How to Run the Project

### 1. Prerequisites
- **Android Studio**: Ladybug (2024.2.1) or newer.
- **Java**: JDK 17 (recommended).
- **Gradle**: 8.13.

### 2. Import the Project
1. Open Android Studio.
2. Select **File > Open** and navigate to the project root directory.
3. Wait for the **Gradle Sync** to finish.

### 3. API Configuration (CRITICAL)
The app will not function correctly without valid API keys. Follow these steps:

#### A. Firebase Setup
1. Go to the [Firebase Console](https://console.firebase.google.com/).
2. Create a new project named `VidyaVahini`.
3. Add an **Android App** to the project:
   - **Package Name**: `com.vidyavahini.app`
   - **SHA-1 Fingerprint**: Required for Phone Auth. Get it by running `./gradlew signingReport` in the Android Studio terminal.
4. Download the `google-services.json` file.
5. Move this file into the `app/` directory of the project.
6. **Enable Services**:
   - **Authentication**: Enable the **Phone** provider.
   - **Realtime Database**: Create a database in your preferred region.
   - **Cloud Messaging**: Setup automatically via the JSON file.

#### B. Google Maps Setup
1. Go to the [Google Cloud Console](https://console.cloud.google.com/).
2. Enable the **Maps SDK for Android**.
3. Create an **API Key** under Credentials.
4. In the project root, open (or create) the `local.properties` file.
5. Add your key: `MAPS_API_KEY=YOUR_KEY_HERE`.

---

## 🛠 Features & Setup Tips

### Empty Route/Stop Dropdowns?
If you are testing on a fresh database, the routes will be empty. I have added an **Auto-Seeding** feature:
- On the Registration screen, the app will check if the database is empty.
- If empty, it will automatically inject dummy routes (e.g., "Pune → Nashik") into your Firebase database.
- **Note**: Ensure your Firebase Database Rules are set to `allow read, write: if true;` during initial development to allow this seeding to work.

### Shared Authentication
The app uses a shared `AuthViewModel` scoped to the Activity. This ensures that the OTP session started in `LoginFragment` is available in `OtpFragment` for verification.

---

## 📱 Running on Emulator/Device
1. Select the **'app'** module in the run configurations.
2. Choose your device/emulator (e.g., `Medium Phone API 36`).
3. Click the green **Run** button.

---

## 🏗 Project Architecture
- **Language**: Kotlin
- **UI**: XML with ViewBinding & Material Design 3.
- **Navigation**: Jetpack Navigation Component.
- **Backend**: Firebase (Auth, RTDB, FCM).
- **Async**: Kotlin Coroutines.
