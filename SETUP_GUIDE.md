# VidyaVahini Project Setup Guide

This guide provides step-by-step instructions to configure **Firebase** and **Google Maps API** for the VidyaVahini project. Follow these steps carefully to ensure all real-time tracking and authentication features work correctly.

---

## 1. Prerequisites
- **Android Studio** (Koala or later recommended)
- A **Google Account** (to access Firebase and Google Cloud Console)
- Your project's SHA-1 fingerprint (see Section 4 for how to get this)

---

## 2. Firebase Configuration

### A. Create a Firebase Project
1. Go to the [Firebase Console](https://console.firebase.google.com/).
2. Click **Add project** and name it `VidyaVahini`.
3. Follow the prompts (Google Analytics is optional).

### B. Register the Android App
1. On the Project Overview page, click the **Android icon** to add an app.
2. **Android package name**: `com.vidyavahini.app` (This must match exactly).
3. **App nickname**: `VidyaVahini`.
4. **Debug signing certificate SHA-1**: *Highly Recommended* (Required for Google Sign-In and Phone Auth).
   - See [Section 4](#4-obtaining-sha-1-fingerprint) to find your SHA-1.
5. Click **Register app**.

### C. Add Configuration File
1. Download the `google-services.json` file.
2. In Android Studio, switch to the **Project** view.
3. Place the file in the `app/` directory of your project.

### D. Enable Firebase Services
In the Firebase Console sidebar, enable the following:
1. **Authentication**:
   - Go to **Build > Authentication > Get Started**.
   - Enable **Email/Password**, **Google**, and **Phone** sign-in methods.
2. **Realtime Database**:
   - Go to **Build > Realtime Database > Create Database**.
   - Choose a location (e.g., `asia-southeast1`).
   - Start in **test mode** (or set rules to allow read/write for authenticated users).
3. **Cloud Messaging (FCM)**:
   - This is enabled by default once the app is registered.

---

## 3. Google Maps API Setup

### A. Enable Maps SDK
1. Go to the [Google Cloud Console](https://console.cloud.google.com/).
2. Select your Firebase project from the dropdown.
3. Go to **APIs & Services > Library**.
4. Search for **"Maps SDK for Android"** and click **Enable**.

### B. Generate API Key
1. Go to **APIs & Services > Credentials**.
2. Click **+ CREATE CREDENTIALS** and select **API key**.
3. Copy your new API key.
4. *Recommended*: Click **Edit API key** to restrict it to "Android apps" using your package name and SHA-1 for security.

### C. Configure Project
1. Open the `local.properties` file in your root project directory.
2. Add the following line, replacing `YOUR_KEY` with the key you just copied:
   ```properties
   MAPS_API_KEY=YOUR_KEY
   ```

---

## 4. Obtaining SHA-1 Fingerprint
To enable Google Sign-In and Phone Auth, Firebase needs your machine's unique SHA-1 key.

1. Open **Android Studio**.
2. Click on the **Gradle** tab (usually on the right sidebar).
3. Navigate to: `VidyaVahini > app > Tasks > android > signingReport`.
4. Double-click **signingReport**.
5. Look at the **Run** window at the bottom. Copy the **SHA-1** value under `Variant: debug`.
6. Add this SHA-1 to your Firebase Project Settings (**Project Settings > General > Your apps**).

---

## 5. Final Steps
1. **Sync Project**: In Android Studio, click **File > Sync Project with Gradle Files**.
2. **Clean & Build**: Click **Build > Clean Project**, then **Build > Rebuild Project**.
3. **Run**: Connect an emulator or physical device and click **Run**.

---

### Tips for Collaborators
- **Demo Mode**: If you are using simulation mode, ensure your Realtime Database is populated. Scripts like `generate_massive_bmtc.py` can help.
- **API Limits**: Be mindful of Google Maps free tier limits during development.
