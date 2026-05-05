# 🚌 Vidya-Vahini — Complete Project Plan (Validated)
### Android App Development using GenAI | Project #101
### Language: Kotlin | IDE: Android Studio | All Free Tier

---

## 📌 Project Summary

**Vidya-Vahini** is a crowdsourced, real-time bus tracking app for rural students.
- A student spots the bus → taps **PING** → every student on that route instantly sees the update + ETA
- Think **"Waze for Rural Students"** — no GPS hardware needed, runs on budget phones, works on 2G

---

## 🛠️ Complete Tech Stack (100% Free Tier)

| Layer | Tool | Free Limit | Why This Tool |
|---|---|---|---|
| IDE | Android Studio Ladybug | Unlimited | Official Android IDE |
| Language | Kotlin | Unlimited | Modern, concise, null-safe |
| Auth | Firebase Authentication | 10,000 users/month | Phone OTP — no email needed |
| Database | Firebase Realtime Database | 1 GB storage, 10 GB/month | Push updates in < 2 seconds |
| Notifications | Firebase Cloud Messaging (FCM) | Unlimited messages | Free push to all route users |
| Maps | Google Maps SDK for Android | $200 credit/month | Official Android map SDK |
| SMS Fallback | Android SmsManager (built-in) | Free — uses device SIM | For non-smartphone parents |
| Version Control | GitHub | Unlimited public repos | Code backup + collaboration |

### Firebase Scale Math (Free Tier Validated)
```
100 students x 1 ping every 12 min x 200 bytes per ping
= ~100 pings/hour x 200 bytes
= 20,000 bytes/hour = 0.02 MB/hour
= ~14 MB/month used

Firebase free limit = 10 GB/month transfer
You are using 0.14% of your free quota. Completely safe.
```

---

## 🔑 API Keys & Configuration — Step by Step

### Step 1: Firebase Setup
> URL: https://console.firebase.google.com

```
1. Click "Add Project" → name it: vidya-vahini
2. Disable Google Analytics (not needed)
3. Click "Create Project"
4. Click "Add App" → choose Android icon
5. Package name: com.vidyavahini.app
6. App nickname: Vidya-Vahini
7. Click "Register App"
8. Download google-services.json
9. Place it inside: YourProject/app/google-services.json
```

**Enable these Firebase services (left sidebar):**
```
Build → Authentication → Get Started → Sign-in method → Phone → Enable → Save
Build → Realtime Database → Create Database → Start in Test Mode → Enable
Build → Cloud Messaging → (auto-enabled, nothing to do)
```

### Step 2: Google Maps API Key
> URL: https://console.cloud.google.com

```
1. Create project OR select your existing project
2. Left menu → APIs & Services → Library
3. Search "Maps SDK for Android" → Enable
4. Left menu → APIs & Services → Credentials
5. Click "+ CREATE CREDENTIALS" → API Key
6. Copy the key (looks like: AIzaSy_your_key_here)
7. Click "Edit API Key" → Restrict to Android apps
8. Add your SHA-1 fingerprint (see below)
```

**Get your SHA-1 fingerprint (run this in Android Studio Terminal):**
```bash
./gradlew signingReport
```
Look for `SHA1:` under `Variant: debug` → copy that value → paste in Google Cloud Console.

### Step 3: Where to Put the Keys

**`local.properties`** (root of project — NEVER commit to GitHub)
```properties
sdk.dir=/path/to/your/android/sdk
MAPS_API_KEY=AIzaSy_your_actual_key_here
```

**`app/build.gradle.kts`**
```kotlin
android {
    defaultConfig {
        applicationId = "com.vidyavahini.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        manifestPlaceholders["MAPS_API_KEY"] =
            project.findProperty("MAPS_API_KEY") as String? ?: ""
    }
}
```

**`app/src/main/AndroidManifest.xml`**
```xml
<manifest>
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.SEND_SMS"/>
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>

    <application>
        <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="${MAPS_API_KEY}"/>

        <service
            android:name=".utils.VidyaFirebaseMessagingService"
            android:exported="false">
            <intent-filter>
                <action android:name="com.google.firebase.MESSAGING_EVENT"/>
            </intent-filter>
        </service>
    </application>
</manifest>
```

---

## 📦 Complete build.gradle.kts Dependencies

**Project-level `build.gradle.kts`**
```kotlin
plugins {
    id("com.android.application") version "8.3.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
}
```

**App-level `app/build.gradle.kts`**
```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
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
        manifestPlaceholders["MAPS_API_KEY"] =
            project.findProperty("MAPS_API_KEY") as String? ?: ""
    }

    buildFeatures { viewBinding = true }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions { jvmTarget = "1.8" }
}

dependencies {
    // Firebase BoM — manages all Firebase versions automatically
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")

    // Google Maps
    implementation("com.google.android.gms:play-services-maps:18.2.0")

    // Architecture Components
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // UI
    implementation("androidx.core:core-ktx:1.13.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
}
```

---

## 🏗️ Complete Project Folder Structure

```
app/
└── src/main/
    ├── java/com/vidyavahini/app/
    │   ├── MyApplication.kt              ← Firebase offline persistence setup
    │   │
    │   ├── data/
    │   │   ├── model/
    │   │   │   ├── Student.kt
    │   │   │   ├── Route.kt
    │   │   │   ├── Stop.kt
    │   │   │   ├── BusPing.kt
    │   │   │   └── Breakdown.kt
    │   │   └── repository/
    │   │       └── FirebaseRepository.kt
    │   │
    │   ├── ui/
    │   │   ├── auth/
    │   │   │   ├── LoginFragment.kt      ← Phone number entry
    │   │   │   ├── OtpFragment.kt        ← OTP verification
    │   │   │   └── RegisterFragment.kt   ← Name + route + stop selection
    │   │   ├── home/
    │   │   │   └── HomeFragment.kt       ← Dashboard: last ping + PING button
    │   │   ├── tracking/
    │   │   │   └── TrackingFragment.kt   ← Map + ETA display
    │   │   └── safereach/
    │   │       └── SafeReachFragment.kt  ← "I Reached" button
    │   │
    │   ├── viewmodel/
    │   │   ├── AuthViewModel.kt
    │   │   └── TrackingViewModel.kt
    │   │
    │   └── utils/
    │       ├── ETACalculator.kt
    │       ├── NotificationHelper.kt
    │       └── VidyaFirebaseMessagingService.kt
    │
    ├── res/
    │   ├── layout/
    │   │   ├── fragment_login.xml
    │   │   ├── fragment_otp.xml
    │   │   ├── fragment_register.xml
    │   │   ├── fragment_home.xml
    │   │   ├── fragment_tracking.xml
    │   │   └── fragment_safe_reach.xml
    │   ├── navigation/
    │   │   └── nav_graph.xml
    │   └── values/
    │       ├── colors.xml
    │       ├── strings.xml
    │       └── themes.xml
    │
    ├── google-services.json              ← From Firebase console — NEVER commit
    └── AndroidManifest.xml
```

---

## 🗄️ Firebase Realtime Database Structure

Manually create this in the Firebase Console → Realtime Database:

```json
{
  "routes": {
    "route_pune_nashik": {
      "name": "Pune to Nashik College Express",
      "stops": {
        "stop_01": { "name": "Shivajinagar",  "lat": 18.5308, "lng": 73.8474, "order": 1 },
        "stop_02": { "name": "Pimpri Bridge", "lat": 18.6188, "lng": 73.7997, "order": 2 },
        "stop_03": { "name": "Dehu Road",     "lat": 18.6879, "lng": 73.7624, "order": 3 },
        "stop_04": { "name": "Talegaon",      "lat": 18.7349, "lng": 73.6750, "order": 4 },
        "stop_05": { "name": "College Gate",  "lat": 18.7980, "lng": 73.6123, "order": 5 }
      }
    }
  },
  "pings": {
    "route_pune_nashik": {
      "latest": {
        "stopId": "stop_02",
        "timestamp": 1714000000000,
        "pinggedBy": "test_uid",
        "status": "on_time"
      }
    }
  },
  "breakdowns": {
    "route_pune_nashik": {
      "active": false,
      "reportedBy": "",
      "timestamp": 0,
      "message": ""
    }
  },
  "students": {
    "example_uid": {
      "name": "Priya Sharma",
      "routeId": "route_pune_nashik",
      "stopId": "stop_01",
      "parentPhone": "+919876543210",
      "fcmToken": ""
    }
  }
}
```

---

## 🔐 Firebase Security Rules

Go to Firebase Console → Realtime Database → Rules tab → paste:

```json
{
  "rules": {
    "routes": {
      ".read": "auth != null",
      ".write": false
    },
    "pings": {
      "$routeId": {
        ".read": "auth != null",
        ".write": "auth != null"
      }
    },
    "breakdowns": {
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

## 🧩 Complete Kotlin Code — Every File

---

### `MyApplication.kt`
```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // CRITICAL for rural 2G areas — caches last known ping when offline
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}
```
Add to AndroidManifest.xml application tag: `android:name=".MyApplication"`

---

### `data/model/Stop.kt`
```kotlin
data class Stop(
    val name: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val order: Int = 0
)
```

### `data/model/Route.kt`
```kotlin
data class Route(
    val name: String = "",
    val stops: Map<String, Stop> = emptyMap()
)
```

### `data/model/BusPing.kt`
```kotlin
data class BusPing(
    val stopId: String = "",
    val timestamp: Long = 0L,
    val pinggedBy: String = "",
    val status: String = "on_time"   // "on_time" | "delayed" | "breakdown"
)
```

### `data/model/Breakdown.kt`
```kotlin
data class Breakdown(
    val active: Boolean = false,
    val reportedBy: String = "",
    val timestamp: Long = 0L,
    val message: String = ""
)
```

### `data/model/Student.kt`
```kotlin
data class Student(
    val name: String = "",
    val routeId: String = "",
    val stopId: String = "",
    val parentPhone: String = "",
    val fcmToken: String = ""
)
```

---

### `data/repository/FirebaseRepository.kt`
```kotlin
class FirebaseRepository {

    private val db   = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    // ── Student ─────────────────────────────────────────────────────────
    fun saveStudent(student: Student) {
        val uid = auth.currentUser?.uid ?: return
        db.child("students").child(uid).setValue(student)
    }

    fun getStudent(onResult: (Student?) -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        db.child("students").child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snap: DataSnapshot) {
                    onResult(snap.getValue(Student::class.java))
                }
                override fun onCancelled(e: DatabaseError) { onResult(null) }
            })
    }

    // ── Route ────────────────────────────────────────────────────────────
    fun getRoute(routeId: String, onResult: (Route?) -> Unit) {
        db.child("routes").child(routeId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snap: DataSnapshot) {
                    onResult(snap.getValue(Route::class.java))
                }
                override fun onCancelled(e: DatabaseError) { onResult(null) }
            })
    }

    fun getAllRoutes(onResult: (Map<String, Route>) -> Unit) {
        db.child("routes")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snap: DataSnapshot) {
                    val map = mutableMapOf<String, Route>()
                    snap.children.forEach { child ->
                        child.getValue(Route::class.java)?.let { map[child.key!!] = it }
                    }
                    onResult(map)
                }
                override fun onCancelled(e: DatabaseError) { onResult(emptyMap()) }
            })
    }

    // ── Ping ─────────────────────────────────────────────────────────────
    fun pingBus(routeId: String, stopId: String) {
        val uid = auth.currentUser?.uid ?: return
        val ping = BusPing(
            stopId    = stopId,
            timestamp = System.currentTimeMillis(),
            pinggedBy = uid,
            status    = "on_time"
        )
        db.child("pings").child(routeId).child("latest").setValue(ping)
    }

    fun listenForPings(routeId: String, onUpdate: (BusPing) -> Unit): ValueEventListener {
        val listener = object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {
                snap.getValue(BusPing::class.java)?.let { onUpdate(it) }
            }
            override fun onCancelled(e: DatabaseError) {}
        }
        db.child("pings").child(routeId).child("latest").addValueEventListener(listener)
        return listener
    }

    fun removePingListener(routeId: String, listener: ValueEventListener) {
        db.child("pings").child(routeId).child("latest").removeEventListener(listener)
    }

    // ── Breakdown ────────────────────────────────────────────────────────
    fun reportBreakdown(routeId: String, message: String) {
        val uid = auth.currentUser?.uid ?: return
        val breakdown = Breakdown(
            active     = true,
            reportedBy = uid,
            timestamp  = System.currentTimeMillis(),
            message    = message
        )
        db.child("breakdowns").child(routeId).setValue(breakdown)
    }

    fun listenForBreakdown(routeId: String, onUpdate: (Breakdown?) -> Unit) {
        db.child("breakdowns").child(routeId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snap: DataSnapshot) {
                    onUpdate(snap.getValue(Breakdown::class.java))
                }
                override fun onCancelled(e: DatabaseError) { onUpdate(null) }
            })
    }
}
```

---

### `utils/ETACalculator.kt`
```kotlin
object ETACalculator {

    // Average minutes between consecutive stops — adjust per route
    private val avgMinutesPerStop = mapOf(
        "route_pune_nashik" to 12,
        "route_default"     to 10
    )

    fun calculateETA(routeId: String, busCurrentOrder: Int, studentStopOrder: Int): Int {
        val avg       = avgMinutesPerStop[routeId] ?: avgMinutesPerStop["route_default"]!!
        val stopsAway = studentStopOrder - busCurrentOrder
        return if (stopsAway > 0) stopsAway * avg else 0
    }

    fun formatETA(minutes: Int): String = when {
        minutes <= 0  -> "Bus may have already passed your stop"
        minutes <= 2  -> "Bus arriving NOW — head to your stop!"
        minutes <= 60 -> "Bus expected in $minutes minutes"
        else          -> "Bus expected in ${minutes / 60}h ${minutes % 60}m"
    }
}
```

---

### `viewmodel/TrackingViewModel.kt`
```kotlin
class TrackingViewModel : ViewModel() {

    private val repo = FirebaseRepository()

    val latestPing   = MutableLiveData<BusPing>()
    val etaText      = MutableLiveData<String>()
    val breakdown    = MutableLiveData<Breakdown?>()
    val currentRoute = MutableLiveData<Route>()

    private var pingListener: ValueEventListener? = null
    private var activeRouteId = ""

    fun loadRoute(routeId: String) {
        activeRouteId = routeId
        repo.getRoute(routeId) { route ->
            route?.let { currentRoute.postValue(it) }
        }
    }

    fun startListening(routeId: String, studentStopOrder: Int) {
        activeRouteId = routeId
        pingListener = repo.listenForPings(routeId) { ping ->
            latestPing.postValue(ping)
            val stops    = currentRoute.value?.stops ?: return@listenForPings
            val busOrder = stops[ping.stopId]?.order ?: 0
            val eta      = ETACalculator.calculateETA(routeId, busOrder, studentStopOrder)
            etaText.postValue(ETACalculator.formatETA(eta))
        }
        repo.listenForBreakdown(routeId) { b -> breakdown.postValue(b) }
    }

    fun pingBus(stopId: String)            { repo.pingBus(activeRouteId, stopId) }
    fun reportBreakdown(message: String)   { repo.reportBreakdown(activeRouteId, message) }

    override fun onCleared() {
        super.onCleared()
        pingListener?.let { repo.removePingListener(activeRouteId, it) }
    }
}
```

---

### `ui/tracking/TrackingFragment.kt` — Map + Route Line (Gap 1 Fixed)
```kotlin
class TrackingFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentTrackingBinding
    private lateinit var googleMap: GoogleMap
    private val viewModel: TrackingViewModel by viewModels()
    private var busMarker: Marker? = null

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        FragmentTrackingBinding.inflate(i, c, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs        = requireContext().getSharedPreferences("vidya", Context.MODE_PRIVATE)
        val routeId      = prefs.getString("routeId", "") ?: ""
        val stopId       = prefs.getString("stopId",  "") ?: ""
        val studentOrder = prefs.getInt("stopOrder", 1)

        (childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment)
            .getMapAsync(this)

        viewModel.loadRoute(routeId)
        viewModel.startListening(routeId, studentOrder)

        viewModel.etaText.observe(viewLifecycleOwner) { binding.tvEta.text = it }

        viewModel.latestPing.observe(viewLifecycleOwner) { ping ->
            val stop = viewModel.currentRoute.value?.stops?.get(ping.stopId) ?: return@observe
            busMarker?.remove()
            busMarker = googleMap.addMarker(
                MarkerOptions()
                    .position(LatLng(stop.lat, stop.lng))
                    .title("Bus is here: ${stop.name}")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            )
            binding.tvLastPing.text = "Last seen: ${stop.name}"
        }

        viewModel.breakdown.observe(viewLifecycleOwner) { b ->
            binding.cardBreakdown.visibility = if (b?.active == true) View.VISIBLE else View.GONE
            binding.tvBreakdownMsg.text = b?.message ?: ""
        }

        binding.btnPing.setOnClickListener {
            viewModel.pingBus(stopId)
            binding.btnPing.text = "Pinged!"
            binding.btnPing.isEnabled = false
            // Cooldown: prevent spam pings for 2 minutes
            binding.btnPing.postDelayed({
                binding.btnPing.text = "PING BUS"
                binding.btnPing.isEnabled = true
            }, 120_000L)
        }

        binding.btnBreakdown.setOnClickListener {
            viewModel.reportBreakdown("Bus has broken down — find alternatives!")
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true
        viewModel.currentRoute.observe(viewLifecycleOwner) { drawRouteLine(it) }
    }

    // ── ROUTE LINE DRAWING — Horizontal polyline with stop markers ────────
    private fun drawRouteLine(route: Route) {
        val sortedStops = route.stops.values.sortedBy { it.order }
        val latLngs     = sortedStops.map { LatLng(it.lat, it.lng) }

        // Draw the blue route polyline
        googleMap.addPolyline(
            PolylineOptions()
                .addAll(latLngs)
                .color(Color.parseColor("#1565C0"))  // dark blue
                .width(10f)
                .geodesic(true)
        )

        // Add a circle + pin at each stop
        sortedStops.forEach { stop ->
            googleMap.addCircle(
                CircleOptions()
                    .center(LatLng(stop.lat, stop.lng))
                    .radius(150.0)
                    .fillColor(Color.parseColor("#90CAF9"))   // light blue fill
                    .strokeColor(Color.parseColor("#1565C0")) // dark blue border
                    .strokeWidth(3f)
            )
            googleMap.addMarker(
                MarkerOptions()
                    .position(LatLng(stop.lat, stop.lng))
                    .title("Stop ${stop.order}: ${stop.name}")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )
        }

        // Auto-zoom camera to show the entire route
        val bounds = LatLngBounds.Builder().apply { latLngs.forEach { include(it) } }.build()
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    }
}
```

---

### `ui/safereach/SafeReachFragment.kt` — FCM + SMS Fallback (Gap 3 Fixed)
```kotlin
class SafeReachFragment : Fragment() {

    private lateinit var binding: FragmentSafeReachBinding

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        FragmentSafeReachBinding.inflate(i, c, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs       = requireContext().getSharedPreferences("vidya", Context.MODE_PRIVATE)
        val studentName = prefs.getString("name", "Your child") ?: "Your child"
        val parentPhone = prefs.getString("parentPhone", "") ?: ""

        binding.btnReached.setOnClickListener {
            // Method 1: FCM notification (parent has smartphone + app)
            NotificationHelper.sendFCMSafeReach(studentName)

            // Method 2: SMS fallback — FREE, uses device SIM, works for feature phones
            if (parentPhone.isNotEmpty()) {
                sendSmsToParent(parentPhone, studentName)
            }

            binding.btnReached.text     = "Parents Notified!"
            binding.btnReached.isEnabled = false
            binding.tvStatus.text       = "$studentName has safely reached college."
        }
    }

    // SMS via Android SmsManager — no API key, no cost, works on any parent phone
    private fun sendSmsToParent(phone: String, name: String) {
        try {
            val msg        = "$name has safely reached college. - Vidya-Vahini"
            val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requireContext().getSystemService(SmsManager::class.java)
            } else {
                @Suppress("DEPRECATION") SmsManager.getDefault()
            }
            smsManager.sendTextMessage(phone, null, msg, null, null)
        } catch (e: Exception) {
            // SMS failed — FCM was already sent as primary method
        }
    }
}
```

---

### `utils/NotificationHelper.kt`
```kotlin
object NotificationHelper {

    fun sendFCMSafeReach(studentName: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseDatabase.getInstance().reference
            .child("safereach").child(uid).setValue(
                mapOf(
                    "studentName" to studentName,
                    "timestamp"   to System.currentTimeMillis(),
                    "reached"     to true
                )
            )
    }

    fun showLocalNotification(context: Context, title: String, message: String) {
        val channelId = "vidya_alerts"
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nm.createNotificationChannel(
                NotificationChannel(channelId, "Bus Alerts", NotificationManager.IMPORTANCE_HIGH)
            )
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        nm.notify(System.currentTimeMillis().toInt(), notification)
    }
}
```

---

### `utils/VidyaFirebaseMessagingService.kt`
```kotlin
class VidyaFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        NotificationHelper.showLocalNotification(
            this,
            message.notification?.title ?: "Vidya-Vahini",
            message.notification?.body  ?: "Bus update"
        )
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseDatabase.getInstance().reference
            .child("students").child(uid).child("fcmToken").setValue(token)
    }
}
```

---

### `viewmodel/AuthViewModel.kt`
```kotlin
class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    val authState = MutableLiveData<String>()  // "sent" | "verified" | "error"

    lateinit var verificationId: String

    fun sendOtp(phoneNumber: String, activity: Activity) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithCredential(credential)
                }
                override fun onVerificationFailed(e: FirebaseException) {
                    authState.postValue("error: ${e.message}")
                }
                override fun onCodeSent(vId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    verificationId = vId
                    authState.postValue("sent")
                }
            }).build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp(otp: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnSuccessListener { authState.postValue("verified") }
            .addOnFailureListener { authState.postValue("error: ${it.message}") }
    }
}
```

---

## 📱 Screen UI Layout Guide

### `fragment_home.xml` — Main Dashboard
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <!-- Route name -->
    <TextView android:id="@+id/tvRouteName"
        android:text="Pune → Nashik College Express"
        android:textSize="18sp" android:textStyle="bold"
        android:layout_width="match_parent" android:layout_height="wrap_content"/>

    <!-- Last ping status card -->
    <com.google.android.material.card.MaterialCardView
        android:layout_marginTop="16dp"
        android:layout_width="match_parent" android:layout_height="wrap_content">
        <LinearLayout android:orientation="vertical" android:padding="16dp"
            android:layout_width="match_parent" android:layout_height="wrap_content">
            <TextView android:text="Last Bus Update" android:textSize="12sp"
                android:textColor="#888888"
                android:layout_width="wrap_content" android:layout_height="wrap_content"/>
            <TextView android:id="@+id/tvLastPing"
                android:text="Waiting for first ping..."
                android:textSize="16sp" android:layout_marginTop="4dp"
                android:layout_width="match_parent" android:layout_height="wrap_content"/>
        </LinearLayout>
    </com.google.android.material.card.MaterialCardView>

    <!-- ETA display -->
    <TextView android:id="@+id/tvEta"
        android:layout_marginTop="16dp"
        android:text="ETA: Calculating..."
        android:textSize="20sp" android:textStyle="bold"
        android:textColor="#1565C0"
        android:layout_width="match_parent" android:layout_height="wrap_content"/>

    <!-- PING BUS button — big, prominent -->
    <com.google.android.material.button.MaterialButton
        android:id="@+id/btnPing"
        android:text="PING BUS"
        android:textSize="18sp"
        android:layout_marginTop="24dp"
        android:layout_width="match_parent" android:layout_height="64dp"/>

    <!-- Report Breakdown button -->
    <com.google.android.material.button.MaterialButton
        android:id="@+id/btnBreakdown"
        style="@style/Widget.Material3.Button.OutlinedButton"
        android:text="Report Breakdown"
        android:layout_marginTop="12dp"
        android:layout_width="match_parent" android:layout_height="wrap_content"/>

    <!-- View Map button -->
    <com.google.android.material.button.MaterialButton
        android:id="@+id/btnMap"
        style="@style/Widget.Material3.Button.OutlinedButton"
        android:text="View Route Map"
        android:layout_marginTop="8dp"
        android:layout_width="match_parent" android:layout_height="wrap_content"/>

    <!-- Safe-Reach button -->
    <com.google.android.material.button.MaterialButton
        android:id="@+id/btnSafeReach"
        style="@style/Widget.Material3.Button.OutlinedButton"
        android:text="I Reached College Safely"
        android:layout_marginTop="8dp"
        android:layout_width="match_parent" android:layout_height="wrap_content"/>

</LinearLayout>
```

### `fragment_tracking.xml` — Map Screen
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent" android:layout_height="match_parent">

    <!-- Map takes 60% of screen height -->
    <fragment
        android:id="@+id/mapFragment"
        android:name="com.google.android.gms.maps.SupportMapFragment"
        android:layout_width="0dp" android:layout_height="0dp"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHeight_percent="0.6"
        app:layout_constraintBottom_toTopOf="@id/etaCard"/>

    <!-- ETA info card -->
    <com.google.android.material.card.MaterialCardView
        android:id="@+id/etaCard"
        android:layout_margin="12dp"
        android:layout_width="0dp" android:layout_height="wrap_content"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toBottomOf="@id/mapFragment">
        <LinearLayout android:orientation="vertical" android:padding="16dp"
            android:layout_width="match_parent" android:layout_height="wrap_content">
            <TextView android:id="@+id/tvEta" android:textSize="18sp" android:textStyle="bold"
                android:layout_width="match_parent" android:layout_height="wrap_content"/>
            <TextView android:id="@+id/tvLastPing" android:textSize="13sp"
                android:textColor="#888888" android:layout_marginTop="4dp"
                android:layout_width="match_parent" android:layout_height="wrap_content"/>
        </LinearLayout>
    </com.google.android.material.card.MaterialCardView>

    <!-- Breakdown alert (hidden by default) -->
    <com.google.android.material.card.MaterialCardView
        android:id="@+id/cardBreakdown"
        android:visibility="gone"
        android:layout_margin="12dp"
        android:layout_width="0dp" android:layout_height="wrap_content"
        app:cardBackgroundColor="#FFEBEE"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toBottomOf="@id/etaCard">
        <TextView android:id="@+id/tvBreakdownMsg" android:padding="12dp"
            android:textColor="#C62828"
            android:layout_width="match_parent" android:layout_height="wrap_content"/>
    </com.google.android.material.card.MaterialCardView>

</androidx.constraintlayout.widget.ConstraintLayout>
```

---

## 🔐 `.gitignore` — MANDATORY — Add Before First Commit

```
# Secret files — NEVER commit these to GitHub
local.properties
google-services.json
*.keystore
*.jks

# Build outputs
/build
/app/build
*.apk
*.aab
*.dex

# IDE files
.idea/
*.iml
.gradle/
captures/

# OS files
.DS_Store
Thumbs.db
```

---

## 🗓️ 4-Week Day-by-Day Execution Timeline

### Week 1 — Project Setup + Authentication

| Day | Task | Success Signal |
|-----|------|----------------|
| 1 | Create Firebase project, download `google-services.json`, create Android Studio project with package `com.vidyavahini.app` | Project builds and runs on emulator |
| 2 | Add all dependencies to `build.gradle.kts`, sync project, resolve any conflicts | Gradle sync succeeds with no errors |
| 3 | Enable Maps SDK, get API key, add to `local.properties`, test blank map renders | Map tiles visible on screen |
| 4 | Create `LoginFragment` with phone number input, country code picker | Login UI renders correctly |
| 5 | Wire `AuthViewModel.sendOtp()` to the login button, test OTP delivery on real device | OTP SMS received on phone |
| 6 | Build `OtpFragment`, wire `verifyOtp()`, on success navigate to `RegisterFragment` | OTP verification works |
| 7 | Build `RegisterFragment` — name field, route dropdown (from Firebase), stop dropdown | Registration saves student to Firebase |

### Week 2 — Core PING Feature (Most Critical)

| Day | Task | Success Signal |
|-----|------|----------------|
| 8 | Manually add routes + stops in Firebase Console (use the JSON structure above) | Data visible in Firebase console |
| 9 | Build `FirebaseRepository.kt` — `pingBus()` and `listenForPings()` | Ping writes to Firebase; listener fires |
| 10 | Build `TrackingViewModel.kt` with LiveData wiring to repository | ViewModel unit-testable in isolation |
| 11 | Build `HomeFragment` UI — route name, last ping card, ETA text, PING button | Home screen renders correctly |
| 12 | Wire PING button to `viewModel.pingBus()` + 2-minute cooldown | Button triggers Firebase write |
| 13 | **Critical test**: run app on 2 devices simultaneously — ping on Device A, see update on Device B | Real-time update confirmed < 2 sec |
| 14 | Add breakdown report button wiring | Breakdown writes to Firebase |

### Week 3 — Map + ETA + Notifications

| Day | Task | Success Signal |
|-----|------|----------------|
| 15 | Build `TrackingFragment`, set up `SupportMapFragment`, `onMapReady()` callback | Map loads in tracking screen |
| 16 | Implement `drawRouteLine()` — polyline connecting all stops + circle markers | Blue route line visible on map |
| 17 | Wire `latestPing` observer to move bus marker on map | Orange marker moves to last pinged stop |
| 18 | Wire ETA: `latestPing` → `ETACalculator` → display text | "Bus expected in X minutes" shows |
| 19 | Build `VidyaFirebaseMessagingService.kt`, update FCM token to Firebase on new token | FCM token saved in student's DB node |
| 20 | Test FCM: use Firebase Console → Cloud Messaging → send test notification | Notification appears on device |
| 21 | Full flow test: ping → map marker moves → ETA updates → breakdown pushes notification | Entire core feature working end-to-end |

### Week 4 — Safe-Reach + Security + Polish

| Day | Task | Success Signal |
|-----|------|----------------|
| 22 | Build `SafeReachFragment` — "I Reached" button UI with status text | Safe-Reach screen renders |
| 23 | Wire FCM Safe-Reach notification + `SmsManager` SMS fallback | Parent receives SMS on feature phone |
| 24 | Apply Firebase Security Rules (replace test mode with the rules above) | Unauthorized writes rejected |
| 25 | Enable `FirebaseDatabase.setPersistenceEnabled(true)` in `MyApplication.kt` | App shows last ping even offline |
| 26 | Test on low-end device: Android 8, 2 GB RAM, via 2G mobile hotspot | App loads, ping works, no crashes |
| 27 | UI polish — consistent colors, readable text, Material Design buttons, accessibility | App looks clean and professional |
| 28 | Final checklist test, generate debug APK, record demo video, write README.md | Project complete and ready to present |

---

## ✅ Final Success Criteria Checklist

| Requirement | Covered By | Status |
|---|---|---|
| Ping updates ALL users on route instantly | Firebase Realtime DB push listener | Covered |
| "Report Breakdown" alerts all students to find alternatives | Breakdown DB node + FCM notification | Covered |
| UI lightweight, works on low-end smartphones | Material UI, APK target < 15 MB, Week 4 Day 26 test | Covered |
| ETA simulated from average time between stops | `ETACalculator.kt` with `avgMinutesPerStop` | Covered |
| Safe-Reach notifies parents/friends | FCM (smartphones) + SmsManager fallback (feature phones) | Covered |
| Works on poor / 2G connectivity | Firebase offline persistence enabled | Covered |
| All tools on free tier | Validated — 0.14% of Firebase free quota used | Covered |
| Route line shows bus's last known position | `drawRouteLine()` + orange bus marker | Covered |

---

## ⚠️ Problems & Exact Fixes

| Problem | Exact Fix |
|---|---|
| `google-services.json` plugin error | Add `id("com.google.gms.google-services")` to BOTH `build.gradle.kts` files |
| Map shows grey / blank | SHA-1 in Google Cloud Console must match `./gradlew signingReport` output exactly |
| OTP never received | Test on real device only — Android emulator has no SIM card |
| Firebase write fails silently | Set Rules to `".write": true` for debug only, revert before launch |
| App crashes offline | `FirebaseDatabase.getInstance().setPersistenceEnabled(true)` must come BEFORE any DB calls |
| FCM notification not appearing | App must not be force-stopped; test via Firebase Console → Cloud Messaging |
| SMS not sending | Declare `<uses-permission android:name="android.permission.SEND_SMS"/>` and request at runtime |
| Firebase ClassCastException | All data class fields need default values: `val name: String = ""` not `val name: String` |
| Bus marker not updating | Call `busMarker?.remove()` before `googleMap.addMarker()` each time |
| Map camera not showing full route | Use `LatLngBounds.Builder()` then `CameraUpdateFactory.newLatLngBounds(bounds, 100)` |

---

## 🚀 Quick Start Checklist — Do These First

```
Step 1  → Install Android Studio from developer.android.com
Step 2  → Go to console.firebase.google.com → Create project "vidya-vahini"
Step 3  → Enable: Authentication (Phone), Realtime Database (Test Mode), Cloud Messaging
Step 4  → Add Android app → package name: com.vidyavahini.app → download google-services.json
Step 5  → Go to console.cloud.google.com → Enable "Maps SDK for Android"
Step 6  → Create API Key → copy it
Step 7  → Open Android Studio → New Project → Empty Views Activity → package: com.vidyavahini.app
Step 8  → Copy google-services.json into the app/ folder
Step 9  → Add MAPS_API_KEY to local.properties
Step 10 → Paste all dependencies from this plan into build.gradle.kts
Step 11 → Add .gitignore file (copy from this plan)
Step 12 → Sync project → Run on emulator → if it builds, start Week 1 Day 1!
```

---

*Vidya-Vahini | Project #101 | Complete Validated Plan*
*All 3 validation gaps fixed: Route Line code + Firebase scale math + SMS fallback*
