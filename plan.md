# 🚌 Vidya-Vahini — Professional Project Plan
### Android App | Kotlin + Jetpack Compose | Material Design 3
### Project #101 — MindMatrix Internship

---

## 📌 Project Vision

**Vidya-Vahini** is a professional-grade, crowdsourced real-time BMTC bus tracking app for Bangalore students. Built with modern Android architecture (MVVM + Clean Architecture), stunning Material Design 3 UI with Lottie animations, and robust offline-first capabilities.

**Tagline:** *"Your Campus Commute, Reimagined"*

### What Makes This Professional

| Aspect | Old Plan (Basic) | New Plan (Professional) |
|--------|-----------------|------------------------|
| Auth | Phone OTP only, no sign-in | Email + Password Sign Up & Sign In, Google Sign-In |
| UI | Basic LinearLayouts | Material 3, Lottie animations, Dark Mode, Shimmer loading |
| Onboarding | Ask destination during register | Beautiful 3-step onboarding with pre-loaded BMTC demo data |
| Architecture | Basic MVVM | Clean Architecture + Repository + UseCases + Hilt DI |
| Data | Manual Firebase entry | Pre-seeded 50+ real BMTC routes with realistic stops |
| Navigation | Basic fragments | Bottom Navigation + Shared Element Transitions |
| UX | Minimal | Haptic feedback, pull-to-refresh, skeleton screens, toasts |
| Offline | Basic persistence | Full offline mode with sync indicators |

---

## 🌟 Complete Feature List

### 1. 🔐 Authentication System (Professional)
- **Sign Up** with Email + Password (Firebase Auth)
- **Sign In** with Email + Password
- **Google Sign-In** (one-tap)
- **Forgot Password** with email reset link
- **Auto Sign-In** — remembers logged-in users
- **Form Validation** — real-time email format, password strength indicator
- **Error Handling** — user-friendly error messages (not Firebase codes)
- **Smooth Transitions** between Sign In ↔ Sign Up screens

### 2. 🎨 Onboarding & Profile Setup
- **Animated Splash Screen** with Lottie bus animation (2 seconds)
- **3-Screen Onboarding Carousel** (skip option):
  - Screen 1: "Track Your Bus in Real-Time" 
  - Screen 2: "Crowdsourced — Students Help Students"
  - Screen 3: "Safe Reach Alerts for Parents"
- **Profile Setup** (after first sign-up only):
  - Enter Name
  - Select College (dropdown with search)
  - Select Bus Route (auto-populated from BMTC demo data)
  - Select Boarding Stop (filtered by selected route)
  - Add Parent's Phone (optional, for Safe Reach SMS)
- **Skip & Complete Later** option for profile

### 3. 🏠 Home Dashboard
- **Greeting Card** — "Good Morning, Priya!" with time-based greeting
- **Live Bus Status Card** — animated pulse when bus is recently pinged
- **ETA Countdown Timer** — real-time countdown with progress ring animation
- **Quick Action Buttons:**
  - 🔔 PING BUS (large, prominent, animated)
  - 🗺️ View Route Map
  - 🚨 Report Issue
  - ✅ Safe Reach
- **Route Info Card** — route name, total stops, your stop number
- **Recent Activity Feed** — last 5 pings on your route with timestamps
- **Pull-to-Refresh** with Material 3 refresh indicator
- **Shimmer Loading** skeleton screens while data loads

### 4. 🗺️ Live Map Tracking
- **Google Maps** with custom styled map (dark mode compatible)
- **Animated Route Polyline** — blue gradient line connecting all stops
- **Stop Markers** — custom circular markers with stop numbers
- **Bus Position Marker** — animated orange bus icon at last pinged stop
- **Your Stop Marker** — highlighted green marker for your boarding point
- **Camera Auto-Focus** — zooms to show bus position + your stop
- **Bottom Sheet** — draggable info panel showing:
  - Current bus location (stop name)
  - ETA to your stop
  - Stops remaining count
  - Last ping timestamp + who pinged
- **Marker Info Windows** — tap any stop to see its name and order

### 5. 📡 PING System (Core Feature)
- **One-Tap PING** — large animated button with ripple effect
- **Stop Selection** — auto-detects your stop, or pick from list
- **Status Tags** — On Time / Delayed / Crowded
- **Cooldown Timer** — 2-minute cooldown with visible countdown
- **Haptic Feedback** — vibration on successful ping
- **Success Animation** — Lottie checkmark animation
- **Real-Time Propagation** — all route students see update in < 2 seconds
- **Ping History** — view recent pings with timestamps

### 6. 🚨 Issue Reporting
- **Report Types:**
  - 🔧 Breakdown
  - ⏰ Heavy Delay (30+ min)
  - 🔄 Route Diverted
  - ❌ Bus Cancelled
- **Alert Banner** — red animated banner on all route users' screens
- **Auto-Dismiss** — alerts expire after 2 hours
- **Confirmation Dialog** — prevents accidental reports

### 7. ✅ Safe Reach Notification
- **"I Reached Safely" Button** — one tap with Lottie success animation
- **Dual Notification:**
  - FCM Push Notification (if parent has smartphone)
  - SMS via device SIM (fallback for feature phones)
- **Daily Reset** — button resets each morning
- **History Log** — parent can see reach history (future scope)

### 8. 👤 Profile & Settings
- **Edit Profile** — name, college, route, stop
- **Switch Route** — change bus route anytime
- **Dark Mode Toggle** — system default / light / dark
- **Notification Preferences** — toggle ping alerts, breakdown alerts
- **Language Selection** — English / Kannada (future scope)
- **About & Help** section
- **Sign Out** with confirmation dialog
- **Delete Account** option

### 9. 🎭 UI/UX Polish (Professional Grade)
- **Material Design 3** — Dynamic color theming
- **Dark Mode** — full dark theme support
- **Lottie Animations:**
  - Splash screen bus animation
  - Ping success checkmark
  - Safe reach celebration
  - Empty state illustrations
  - Loading states
- **Micro-Animations:**
  - Button press scale + ripple
  - Card elevation on touch
  - Smooth fragment transitions
  - Bottom nav icon animations
  - Pull-to-refresh spring physics
- **Shimmer Loading** — skeleton screens on all data-loading screens
- **Custom Snackbars** — styled success/error/info messages
- **Haptic Feedback** — on all primary actions

### 10. 📶 Offline-First Design
- **Firebase Offline Persistence** — cached data available offline
- **Network Status Banner** — "You're offline" indicator
- **Queued Pings** — pings sent when back online
- **Last Known State** — always shows last known bus position
- **Graceful Degradation** — all screens functional offline with cached data

---

## 🏗️ Architecture & Tech Stack

### Tech Stack (100% Free Tier)

| Layer | Technology | Purpose |
|-------|-----------|---------|
| Language | Kotlin 1.9+ | Modern, null-safe, coroutines |
| UI | XML + Material 3 | Professional UI components |
| Architecture | MVVM + Clean Architecture | Scalable, testable |
| DI | Hilt | Dependency injection |
| Auth | Firebase Auth (Email + Google) | Sign Up, Sign In, Reset |
| Database | Firebase Realtime Database | Real-time sync, offline cache |
| Push | Firebase Cloud Messaging | Instant notifications |
| Maps | Google Maps SDK | Route visualization |
| SMS | Android SmsManager | Free SMS via device SIM |
| Animations | Lottie + Material Motion | Professional animations |
| Images | Glide | Efficient image loading |
| Navigation | Jetpack Navigation Component | Single-activity navigation |

### Architecture Diagram
```
┌─────────────────────────────────────────────┐
│                 UI Layer                     │
│  Fragments / Activities / XML Layouts       │
│  Material 3 + Lottie + Shimmer              │
└──────────────────┬──────────────────────────┘
                   │ observes LiveData/StateFlow
┌──────────────────▼──────────────────────────┐
│              ViewModel Layer                 │
│  AuthViewModel, HomeViewModel,               │
│  TrackingViewModel, ProfileViewModel         │
└──────────────────┬──────────────────────────┘
                   │ calls UseCases
┌──────────────────▼──────────────────────────┐
│              Domain Layer                    │
│  PingBusUseCase, GetRouteUseCase,           │
│  ReportIssueUseCase, SafeReachUseCase       │
└──────────────────┬──────────────────────────┘
                   │ calls Repository
┌──────────────────▼──────────────────────────┐
│              Data Layer                      │
│  FirebaseAuthRepository                      │
│  RouteRepository, PingRepository             │
│  StudentRepository                           │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│           Firebase Backend                   │
│  Auth │ Realtime DB │ FCM │ Storage         │
└─────────────────────────────────────────────┘
```

### Project Structure
```
app/src/main/java/com/vidyavahini/app/
├── VidyaVahiniApp.kt                 // Application class
├── di/
│   ├── AppModule.kt                  // Hilt module
│   └── RepositoryModule.kt
├── data/
│   ├── model/
│   │   ├── Student.kt
│   │   ├── Route.kt
│   │   ├── Stop.kt
│   │   ├── BusPing.kt
│   │   ├── BusIssue.kt
│   │   └── SafeReachEvent.kt
│   ├── repository/
│   │   ├── AuthRepository.kt
│   │   ├── RouteRepository.kt
│   │   ├── PingRepository.kt
│   │   └── StudentRepository.kt
│   └── datasource/
│       └── FirebaseDataSource.kt
├── domain/
│   └── usecase/
│       ├── SignInUseCase.kt
│       ├── SignUpUseCase.kt
│       ├── GetRoutesUseCase.kt
│       ├── PingBusUseCase.kt
│       ├── ListenPingsUseCase.kt
│       ├── ReportIssueUseCase.kt
│       └── SafeReachUseCase.kt
├── ui/
│   ├── splash/
│   │   └── SplashActivity.kt
│   ├── onboarding/
│   │   └── OnboardingActivity.kt
│   ├── auth/
│   │   ├── SignInFragment.kt
│   │   ├── SignUpFragment.kt
│   │   └── ForgotPasswordFragment.kt
│   ├── setup/
│   │   └── ProfileSetupFragment.kt
│   ├── home/
│   │   └── HomeFragment.kt
│   ├── tracking/
│   │   └── TrackingFragment.kt
│   ├── ping/
│   │   └── PingFragment.kt
│   ├── issues/
│   │   └── ReportIssueFragment.kt
│   ├── safereach/
│   │   └── SafeReachFragment.kt
│   ├── profile/
│   │   └── ProfileFragment.kt
│   ├── settings/
│   │   └── SettingsFragment.kt
│   └── common/
│       ├── ShimmerView.kt
│       └── NetworkBanner.kt
├── viewmodel/
│   ├── AuthViewModel.kt
│   ├── HomeViewModel.kt
│   ├── TrackingViewModel.kt
│   └── ProfileViewModel.kt
├── utils/
│   ├── ETACalculator.kt
│   ├── NotificationHelper.kt
│   ├── NetworkMonitor.kt
│   ├── HapticHelper.kt
│   └── VidyaFirebaseMessagingService.kt
└── res/
    ├── layout/                       // All XML layouts
    ├── navigation/nav_graph.xml
    ├── anim/                         // Transition animations
    ├── raw/                          // Lottie JSON files
    ├── values/
    │   ├── colors.xml
    │   ├── strings.xml
    │   ├── themes.xml
    │   └── dimens.xml
    └── values-night/
        └── colors.xml                // Dark mode colors
```
## 🎯 Demo Data Strategy (Pre-Seeded — No Manual Entry)

### Demo User Accounts (Pre-created in Firebase Auth)
```
Email: priya.demo@vidyavahini.app    Password: Demo@123    Name: Priya Sharma
Email: rahul.demo@vidyavahini.app    Password: Demo@123    Name: Rahul Kumar  
Email: ananya.demo@vidyavahini.app   Password: Demo@123    Name: Ananya Rao
```

### Pre-Seeded BMTC Routes (50 Real Bangalore Routes)

The app ships with **50 realistic BMTC bus routes** pre-loaded. Here are the key demo routes:

#### Route 1: KBS → Majestic → BMS College (Route 401D)
| Stop# | Stop Name | Lat | Lng |
|-------|----------|-----|-----|
| 1 | Kempegowda Bus Station | 12.9779 | 77.5713 |
| 2 | Majestic Metro | 12.9766 | 77.5713 |
| 3 | Anand Rao Circle | 12.9850 | 77.5720 |
| 4 | Race Course Road | 12.9890 | 77.5680 |
| 5 | Seshadripuram | 12.9920 | 77.5740 |
| 6 | Basavanagudi | 12.9435 | 77.5710 |
| 7 | Bull Temple Road | 12.9430 | 77.5680 |
| 8 | BMS College Gate | 12.9410 | 77.5650 |

#### Route 2: Electronic City → Silk Board → RV College (Route 500CA)
| Stop# | Stop Name | Lat | Lng |
|-------|----------|-----|-----|
| 1 | Electronic City Phase 1 | 12.8456 | 77.6603 |
| 2 | Infosys Gate | 12.8440 | 77.6580 |
| 3 | Bommanahalli | 12.8890 | 77.6240 |
| 4 | BTM Layout | 12.9166 | 77.6101 |
| 5 | Silk Board Junction | 12.9177 | 77.6233 |
| 6 | Jayanagar 4th Block | 12.9250 | 77.5830 |
| 7 | South End Circle | 12.9370 | 77.5750 |
| 8 | RV College of Engineering | 12.9237 | 77.4987 |

#### Route 3: Whitefield → KR Puram → PES University (Route 335E)
| Stop# | Stop Name | Lat | Lng |
|-------|----------|-----|-----|
| 1 | Whitefield Bus Stand | 12.9698 | 77.7500 |
| 2 | ITPL Main Road | 12.9854 | 77.7310 |
| 3 | Marathahalli Bridge | 12.9591 | 77.7019 |
| 4 | KR Puram Railway | 12.9969 | 77.6970 |
| 5 | Tin Factory | 12.9935 | 77.6620 |
| 6 | Indiranagar | 12.9784 | 77.6408 |
| 7 | MG Road | 12.9756 | 77.6095 |
| 8 | PES University | 12.9344 | 77.5350 |

### Pre-Seeded Demo Students
```json
{
  "students": {
    "demo_uid_priya": {
      "name": "Priya Sharma",
      "email": "priya.demo@vidyavahini.app",
      "college": "BMS College of Engineering",
      "routeId": "route_401d",
      "stopId": "stop_05",
      "stopOrder": 5,
      "parentPhone": "+919876543210",
      "profileComplete": true
    },
    "demo_uid_rahul": {
      "name": "Rahul Kumar",
      "email": "rahul.demo@vidyavahini.app",
      "college": "RV College of Engineering",
      "routeId": "route_500ca",
      "stopId": "stop_07",
      "stopOrder": 7,
      "parentPhone": "+919876543211",
      "profileComplete": true
    }
  }
}
```

### Bus Simulator for Demo
A built-in **Demo Mode** simulates a bus moving along a route:
- Toggle in Settings → "Enable Demo Simulation"
- Bus auto-pings every 30 seconds, advancing one stop
- Realistic ETA countdown updates
- Demonstrates full real-time tracking without needing real users

---

## 🗄️ Firebase Database Structure (Enhanced)

```json
{
  "routes": {
    "route_401d": {
      "routeNumber": "401D",
      "name": "KBS → BMS College Express",
      "college": "BMS College of Engineering",
      "frequency": "Every 15 min",
      "firstBus": "06:30 AM",
      "lastBus": "09:00 PM",
      "stops": {
        "stop_01": { "name": "Kempegowda Bus Station", "lat": 12.9779, "lng": 77.5713, "order": 1 },
        "stop_02": { "name": "Majestic Metro", "lat": 12.9766, "lng": 77.5713, "order": 2 },
        "stop_03": { "name": "Anand Rao Circle", "lat": 12.9850, "lng": 77.5720, "order": 3 }
      }
    }
  },
  "pings": {
    "route_401d": {
      "latest": {
        "stopId": "stop_03",
        "stopName": "Anand Rao Circle",
        "timestamp": 1714000000000,
        "pingedBy": "demo_uid_priya",
        "pingedByName": "Priya S.",
        "status": "on_time",
        "crowdLevel": "moderate"
      },
      "history": {
        "ping_001": { "stopId": "stop_02", "timestamp": 1713999400000, "status": "on_time" },
        "ping_002": { "stopId": "stop_01", "timestamp": 1713998800000, "status": "on_time" }
      }
    }
  },
  "issues": {
    "route_401d": {
      "active": false,
      "type": "",
      "reportedBy": "",
      "reportedByName": "",
      "timestamp": 0,
      "message": "",
      "expiresAt": 0
    }
  },
  "safereach": {
    "demo_uid_priya": {
      "studentName": "Priya Sharma",
      "reached": true,
      "timestamp": 1714002000000,
      "college": "BMS College of Engineering"
    }
  },
  "colleges": {
    "bms": { "name": "BMS College of Engineering", "lat": 12.9410, "lng": 77.5650 },
    "rvce": { "name": "RV College of Engineering", "lat": 12.9237, "lng": 77.4987 },
    "pes": { "name": "PES University", "lat": 12.9344, "lng": 77.5350 }
  }
}
```

---

## 📦 Dependencies (build.gradle.kts)

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.vidyavahini.app"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.vidyavahini.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures { viewBinding = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    // Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")

    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:21.0.0")

    // Google Maps
    implementation("com.google.android.gms:play-services-maps:18.2.0")

    // Hilt DI
    implementation("com.google.dagger:hilt-android:2.51")
    kapt("com.google.dagger:hilt-compiler:2.51")

    // Architecture Components
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // UI
    implementation("androidx.core:core-ktx:1.13.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Lottie Animations
    implementation("com.airbnb.android:lottie:6.4.0")

    // Shimmer Loading
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    // Glide (images)
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // ViewPager2 (onboarding)
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // SwipeRefreshLayout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
}
```

---

## 📱 Screen-by-Screen Flow

### Flow 1: First-Time User
```
App Launch → Splash (Lottie, 2s) → Onboarding (3 slides) → Sign Up Screen
→ Enter Email + Password → Create Account → Profile Setup (Name, College, Route, Stop)
→ Home Dashboard
```

### Flow 2: Returning User  
```
App Launch → Splash (Lottie, 2s) → Auto Sign-In Check → Home Dashboard
```

### Flow 3: Daily Usage
```
Home Dashboard → See ETA → Tap "PING BUS" → Select current stop → Confirm
→ All route students see update → View Map → See bus position → Safe Reach
```

### Bottom Navigation (4 tabs)
```
🏠 Home  |  🗺️ Map  |  📡 Ping  |  👤 Profile
```

---

## 🎨 Design System

### Color Palette
```xml
<!-- Primary: Deep Transit Blue -->
<color name="primary">#1565C0</color>
<color name="primary_variant">#0D47A1</color>
<color name="primary_light">#42A5F5</color>

<!-- Secondary: Vibrant Amber -->
<color name="secondary">#FF8F00</color>
<color name="secondary_light">#FFB300</color>

<!-- Accent: Success Green -->
<color name="success">#2E7D32</color>
<color name="error">#C62828</color>
<color name="warning">#EF6C00</color>

<!-- Surface -->
<color name="surface">#FAFAFA</color>
<color name="surface_card">#FFFFFF</color>
<color name="on_surface">#1B1B1F</color>

<!-- Dark Mode -->
<color name="dark_background">#121212</color>
<color name="dark_surface">#1E1E1E</color>
<color name="dark_card">#2C2C2C</color>
```

### Typography (Google Fonts: Inter + Outfit)
```
Headlines:  Outfit Bold 24sp
Subtitles:  Outfit SemiBold 18sp  
Body:       Inter Regular 16sp
Caption:    Inter Regular 12sp
Button:     Inter SemiBold 14sp (ALL CAPS)
```

### Component Styling
- **Cards**: 16dp corner radius, 2dp elevation, 16dp padding
- **Buttons**: 28dp corner radius, 48dp min height, ripple effect
- **Inputs**: Outlined style, 12dp corner radius, helper text below
- **Bottom Nav**: Animated icon transitions, badge support
- **Snackbars**: Rounded 12dp, custom colors per type

---

## 🔐 Firebase Security Rules

```json
{
  "rules": {
    "routes": {
      ".read": "auth != null",
      ".write": false
    },
    "colleges": {
      ".read": "auth != null",
      ".write": false
    },
    "pings": {
      "$routeId": {
        ".read": "auth != null",
        ".write": "auth != null"
      }
    },
    "issues": {
      "$routeId": {
        ".read": "auth != null",
        ".write": "auth != null"
      }
    },
    "students": {
      "$uid": {
        ".read": "$uid === auth.uid",
        ".write": "$uid === auth.uid"
      }
    },
    "safereach": {
      "$uid": {
        ".read": "$uid === auth.uid",
        ".write": "$uid === auth.uid"
      }
    }
  }
}
```

---

## 🔑 API Keys & Configuration

### Firebase Setup
1. Create project at console.firebase.google.com → "vidya-vahini"
2. Enable: **Authentication** (Email/Password + Google) 
3. Enable: **Realtime Database** (Start in Test Mode)
4. Enable: **Cloud Messaging** (auto-enabled)
5. Add Android app → package: `com.vidyavahini.app`
6. Download `google-services.json` → place in `app/`

### Google Maps API
1. console.cloud.google.com → Enable "Maps SDK for Android"
2. Create API Key → Restrict to Android apps + your SHA-1
3. Add to `local.properties`: `MAPS_API_KEY=AIzaSy_your_key`

### Google Sign-In
1. Firebase Console → Authentication → Sign-in method → Google → Enable
2. Add SHA-1 fingerprint (from `./gradlew signingReport`)
3. Web Client ID auto-generated — use in `GoogleSignInOptions`
## 🗓️ 4-Week Implementation Timeline

### Week 1 — Foundation + Auth (Days 1-7)

| Day | Task | Success Signal |
|-----|------|----------------|
| 1 | Project setup: Android Studio, Firebase project, google-services.json, all dependencies | Gradle syncs, app runs on emulator |
| 2 | Hilt DI setup, Application class, base architecture (repositories, modules) | DI compiles, no Hilt errors |
| 3 | Splash screen with Lottie animation + 3-screen onboarding ViewPager2 | Splash → Onboarding flow works |
| 4 | Sign Up screen: email + password fields, validation, MaterialTextInput styling | Form validates email format + password strength |
| 5 | Sign In screen: email + password, "Forgot Password" link, error messages | Sign in works, wrong password shows error |
| 6 | Google Sign-In integration (one-tap) + auto sign-in for returning users | Google button signs in, app remembers login |
| 7 | Profile Setup: name, college dropdown, route selection, stop selection | Profile saves to Firebase, navigates to Home |

### Week 2 — Core Features: Dashboard + PING (Days 8-14)

| Day | Task | Success Signal |
|-----|------|----------------|
| 8 | Seed Firebase with 50 BMTC routes + demo data using Python script | Data visible in Firebase Console |
| 9 | Home Dashboard layout: greeting card, ETA card, quick actions, shimmer loading | Dashboard renders with all cards |
| 10 | PING system: PingBusUseCase, PingRepository, ping button with cooldown | Ping writes to Firebase, cooldown works |
| 11 | Real-time ping listener: LiveData observers, ETA calculation, countdown timer | ETA updates when someone pings |
| 12 | Bottom Navigation setup (Home, Map, Ping, Profile) + fragment transitions | All tabs navigate correctly with animations |
| 13 | Issue reporting: type selection dialog, active alert banner, auto-expire | Breakdown alert shows on all route screens |
| 14 | **2-Device Test**: Ping on Device A → update appears on Device B in < 2s | Real-time sync confirmed |

### Week 3 — Map + Notifications + Safe Reach (Days 15-21)

| Day | Task | Success Signal |
|-----|------|----------------|
| 15 | Map screen: Google Maps, custom style, route polyline, stop markers | Map loads with blue route line |
| 16 | Bus position marker (animated), your-stop marker (green), camera auto-zoom | Orange bus marker moves on ping |
| 17 | Map bottom sheet: draggable panel with ETA, stops remaining, last ping info | Bottom sheet slides up/down smoothly |
| 18 | FCM setup: messaging service, token management, local notifications | Push notification appears on device |
| 19 | Safe Reach: "I Reached" button, Lottie celebration, FCM notification | Button triggers parent notification |
| 20 | SMS fallback: SmsManager integration, runtime permission, feature phone support | SMS sent to parent's number |
| 21 | Demo Mode simulator: auto-ping bus advancing through stops every 30s | Bus moves on map automatically |

### Week 4 — Polish + Dark Mode + Final Testing (Days 22-28)

| Day | Task | Success Signal |
|-----|------|----------------|
| 22 | Dark mode: night colors, theme toggle in settings, dynamic switching | App switches theme without restart |
| 23 | Animations: Lottie for empty states, loading, success; micro-animations on buttons | All animations play smoothly |
| 24 | Shimmer loading on all screens, pull-to-refresh, haptic feedback | Skeleton screens show while loading |
| 25 | Profile screen: edit info, switch route, notification prefs, sign out | All settings functional |
| 26 | Offline mode: network banner, cached data display, queued pings | App works offline, syncs when back |
| 27 | Low-end device test: Android 8, 2GB RAM, 2G network simulation | No crashes, loads in < 3s |
| 28 | Final polish, generate debug APK, record demo video, update README | APK < 15MB, demo video recorded |

---

## 🧪 Demo Presentation Script (5 Minutes)

### Step 1: First Launch (30s)
- Show animated splash screen
- Swipe through 3 onboarding screens
- Click "Get Started"

### Step 2: Sign Up + Profile (60s)
- Create account with email + password
- Show password strength indicator
- Complete profile: select "BMS College", Route 401D, "Seshadripuram" stop
- Show OR sign in with Google button

### Step 3: Home Dashboard (45s)
- Show greeting card with student name
- Point out shimmer loading → data loads
- Show ETA countdown timer
- Demonstrate pull-to-refresh

### Step 4: PING Bus (60s)
- Tap "PING BUS" → select stop → confirm
- Show Lottie success animation + haptic feedback
- Show cooldown timer on button
- **On second device**: show ping update appearing in < 2 seconds
- Show ETA recalculating

### Step 5: Live Map (45s)
- Navigate to Map tab
- Show route polyline with stop markers
- Show animated bus marker at last pinged stop
- Show your-stop highlighted in green
- Drag bottom sheet to see ETA details

### Step 6: Issue Report (30s)
- Tap "Report Issue" → select "Breakdown"
- Show red alert banner appearing
- Explain auto-expiry after 2 hours

### Step 7: Safe Reach (30s)
- Navigate to Safe Reach
- Tap "I Reached Safely"
- Show Lottie celebration animation
- Explain SMS sent to parent's phone

### Step 8: Dark Mode (15s)
- Go to Profile → Settings → Toggle Dark Mode
- Show entire app in dark theme

### Step 9: Offline Mode (15s)
- Enable airplane mode
- Show "You're offline" banner
- Show cached data still displayed
- Ping queued for when back online

---

## ✅ Professional Quality Checklist

| Category | Requirement | Implementation |
|----------|-------------|----------------|
| **Auth** | Email Sign Up | Firebase createUserWithEmailAndPassword |
| **Auth** | Email Sign In | Firebase signInWithEmailAndPassword |
| **Auth** | Google Sign-In | Google One-Tap + Firebase credential |
| **Auth** | Forgot Password | Firebase sendPasswordResetEmail |
| **Auth** | Auto Sign-In | Check currentUser on app launch |
| **UI** | Material Design 3 | Material Components library |
| **UI** | Dark Mode | values-night resources + toggle |
| **UI** | Lottie Animations | 5+ animations (splash, success, empty, loading) |
| **UI** | Shimmer Loading | Facebook Shimmer on all data screens |
| **UI** | Micro-Animations | Button scale, card elevation, transitions |
| **UX** | Haptic Feedback | HapticHelper on all primary actions |
| **UX** | Pull-to-Refresh | SwipeRefreshLayout on Home |
| **UX** | Error Messages | User-friendly, not Firebase codes |
| **UX** | Onboarding | 3-screen carousel, shown once |
| **Core** | Real-Time Ping | Firebase listener, < 2s propagation |
| **Core** | ETA Calculation | ETACalculator with countdown timer |
| **Core** | Map Tracking | Google Maps + polyline + markers |
| **Core** | Issue Reporting | 4 issue types, alert banner |
| **Core** | Safe Reach | FCM + SMS dual notification |
| **Core** | Demo Mode | Built-in bus simulator |
| **Offline** | Persistence | Firebase offline cache enabled |
| **Offline** | Network Banner | ConnectivityManager monitor |
| **Arch** | MVVM | ViewModel + LiveData |
| **Arch** | Clean Architecture | UseCases + Repository pattern |
| **Arch** | Dependency Injection | Hilt modules |

---

## ⚠️ Known Issues & Fixes

| Problem | Fix |
|---------|-----|
| Google Sign-In fails | Add both SHA-1 AND SHA-256 to Firebase project settings |
| Email auth error "account exists" | Enable Email Enumeration Protection in Firebase |
| Lottie not rendering | Ensure .json files in res/raw, not assets |
| Shimmer persists | Stop shimmer in onDataLoaded callback |
| Dark mode flickers | Use AppCompatDelegate.setDefaultNightMode() in Application |
| Hilt "entry point" crash | Ensure @AndroidEntryPoint on Activity AND Fragments |
| Map blank | Verify SHA-1 matches, Maps SDK enabled in Cloud Console |
| FCM token null | Request notification permission on Android 13+ |
| SMS permission denied | Request SEND_SMS at runtime, show rationale dialog |
| Offline ping lost | Firebase persistence queues writes automatically |

---

## 📚 Reference Repositories

| Repository | What to Learn |
|-----------|---------------|
| [android/nowinandroid](https://github.com/android/nowinandroid) | Gold standard architecture, M3, modularization |
| [android/architecture-samples](https://github.com/android/architecture-samples) | MVVM patterns, testing |
| [OneBusAway/onebusaway-android](https://github.com/OneBusAway/onebusaway-android) | Production transit app patterns |
| [javdc/TussApp](https://github.com/javdc/TussApp) | Modern transit app with Clean Architecture |
| [airbnb/lottie-android](https://github.com/airbnb/lottie-android) | Animation implementation |
| [facebook/shimmer-android](https://github.com/facebook/shimmer-android) | Shimmer loading effects |

---

## 🚀 Quick Start (First 30 Minutes)

```
1. Install Android Studio Hedgehog (latest)
2. Firebase Console → Create "vidya-vahini" project
3. Enable: Auth (Email + Google), Realtime DB, Cloud Messaging
4. Add Android app → package: com.vidyavahini.app
5. Download google-services.json → app/ folder
6. Google Cloud Console → Enable Maps SDK → Create API Key
7. local.properties → add MAPS_API_KEY=your_key
8. Paste dependencies from this plan → Sync Gradle
9. Run app → verify splash screen loads
10. Start Week 1 Day 1!
```

---

## 🎯 What This Delivers for Demo

1. **Professional Auth** — Email/Password + Google Sign-In (not just phone OTP)
2. **Stunning UI** — Material 3 + Dark Mode + Lottie + Shimmer (not basic layouts)
3. **Real BMTC Data** — 50 Bangalore routes pre-loaded (not manual entry)
4. **Working Demo** — Built-in simulator moves bus in real-time (no real users needed)
5. **Full Feature Set** — Ping, Map, ETA, Issues, Safe Reach, Offline (all functional)
6. **Production Architecture** — MVVM + Clean Architecture + Hilt (not spaghetti code)

---

*Vidya-Vahini v2.0 | Project #101 | Professional-Grade Plan*  
*Architecture: MVVM + Clean Architecture + Hilt DI*  
*UI: Material Design 3 + Lottie + Shimmer + Dark Mode*  
*Inspired by: NowInAndroid, OneBusAway, TussApp*
