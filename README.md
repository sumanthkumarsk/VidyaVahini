# 🚌 VidyaVahini

**Real-time Community-Driven College Bus Tracking**

VidyaVahini is a premium Android application designed to solve the daily uncertainty of college bus commutes. By leveraging a crowdsourced "ping" system and community-driven updates, it provides students with high-accuracy tracking, visual progress indicators, and real-time alerts—even in areas with low GPS reliability.

---

## ✨ Key Features

### 📡 Crowdsourced Real-time Tracking
Students on the bus "ping" their current location, providing instant updates to everyone waiting at subsequent stops. This decentralized approach ensures tracking works even when dedicated GPS hardware is unavailable.

### 🎮 Gamified Contributions
To encourage active data sharing, students earn **Contribution Points** for every bus ping. These points are displayed on the user profile, fostering a helpful community of "Super-Pingers."

### 🛤️ Live Journey Progress
A sleek, visual progress bar on the dashboard shows exactly how close the bus is to your stop. 
*   **Contextual Status**: Smart labels like *"🚨 Almost there!"* or *"🏁 Bus has reached your stop!"* provide instant situational awareness.

### 💬 Community Pulse Feed
A route-specific real-time feed where students share vital context that maps can't show, such as:
*   "Bus is very crowded today."
*   "Traffic jam near Silk Board."
*   "Bus delayed due to rain."

### 🛡️ Safe Reach & Breakdown Alerts
*   **Safe Reach**: One-tap confirmation to notify guardians/admins of arrival.
*   **Breakdown Reporting**: Instant route-wide alerts if a bus encounters mechanical issues.

### 🔍 Smart Route Selection
A searchable, two-step selection flow (Route → Stop) that makes onboarding and route switching effortless.

---

## 🏗️ Technical Architecture

The app is built using modern Android development best practices:

- **Language**: 100% Kotlin
- **Architecture**: MVVM (Model-View-ViewModel) + Clean Architecture principles.
- **Dependency Injection**: **Hilt** for robust, testable code.
- **Backend**: **Firebase Realtime Database** for sub-second synchronization.
- **UI/UX**: **Material 3 (M3)** with custom animations and a premium glassmorphism-inspired design.
- **Navigation**: Jetpack Navigation Component with a Single-Activity architecture.

---

## 🛠️ Tech Stack

| Component | Technology |
| :--- | :--- |
| **Database** | Firebase Realtime Database |
| **Auth** | Firebase Authentication |
| **Networking** | Coroutines & Flow |
| **UI** | Material Design 3, ViewBinding |
| **Dependency Injection** | Hilt |
| **Navigation** | Jetpack Navigation |

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (or newer)
- A Firebase Project (with Realtime Database and Auth enabled)
- `google-services.json` placed in the `app/` directory

### Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/sumanthkumarsk/VidyaVahini.git
   ```
2. Open the project in Android Studio.
3. Sync Gradle and ensure all dependencies are downloaded.
4. Run the app on an emulator or physical device.

### Demo Mode
For demonstration purposes, the app includes a **Built-in Simulation Engine**. 
1. Select a route (e.g., *335-E*).
2. The app will automatically begin a "Live Demo" simulation, pinging stops sequentially to demonstrate the ETA calculation and progress tracking logic.

---

## 📸 UI Showcase
*Modern, clean, and intuitive Material 3 interfaces.*

- **Dashboard**: Real-time ETA, Progress Bar, and Community Feed.
- **Profile**: Contribution score and route management.
- **Search**: Fast, searchable route and stop selection.

---

## 📄 License
This project is part of the MindMatrix Internship Project. All rights reserved.

---
*Developed with ❤️ by the VidyaVahini Team.*
