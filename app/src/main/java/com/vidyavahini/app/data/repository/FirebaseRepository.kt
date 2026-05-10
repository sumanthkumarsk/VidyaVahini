package com.vidyavahini.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vidyavahini.app.data.model.Breakdown
import com.vidyavahini.app.data.model.BusPing
import com.vidyavahini.app.data.model.Route
import com.vidyavahini.app.data.model.RouteUpdate
import com.vidyavahini.app.data.model.Stop
import com.vidyavahini.app.data.model.Student
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single source of truth for all Firebase Realtime Database operations.
 * All reads/writes pass through this class — ViewModels never touch Firebase directly.
 */
@Singleton
class FirebaseRepository @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val auth: FirebaseAuth
) {
    private val db = firebaseDatabase.reference

    // ── Dummy BMTC Data (used if Firebase is empty) ──────────────────────────

    private fun getDummyRoutes(): Map<String, Route> = mapOf(
        "route_401d" to Route(
            routeNumber = "401D",
            name = "401D: KBS → BMS College Express",
            college = "BMS College of Engineering",
            frequency = "Every 15 min",
            firstBus = "06:30 AM",
            lastBus = "09:00 PM",
            stops = mapOf(
                "stop_01" to Stop("Kempegowda Bus Station", 12.9779, 77.5713, 1),
                "stop_02" to Stop("Majestic Metro", 12.9766, 77.5713, 2),
                "stop_03" to Stop("Anand Rao Circle", 12.9850, 77.5720, 3),
                "stop_04" to Stop("Race Course Road", 12.9890, 77.5680, 4),
                "stop_05" to Stop("Seshadripuram", 12.9920, 77.5740, 5),
                "stop_06" to Stop("Basavanagudi", 12.9435, 77.5710, 6),
                "stop_07" to Stop("Bull Temple Road", 12.9430, 77.5680, 7),
                "stop_08" to Stop("BMS College Gate", 12.9410, 77.5650, 8)
            )
        ),
        "route_500ca" to Route(
            routeNumber = "500CA",
            name = "500CA: Electronic City → RV College",
            college = "RV College of Engineering",
            frequency = "Every 20 min",
            firstBus = "06:00 AM",
            lastBus = "08:30 PM",
            stops = mapOf(
                "stop_01" to Stop("Electronic City Phase 1", 12.8456, 77.6603, 1),
                "stop_02" to Stop("Infosys Gate", 12.8440, 77.6580, 2),
                "stop_03" to Stop("Bommanahalli", 12.8890, 77.6240, 3),
                "stop_04" to Stop("BTM Layout", 12.9166, 77.6101, 4),
                "stop_05" to Stop("Silk Board Junction", 12.9177, 77.6233, 5),
                "stop_06" to Stop("Jayanagar 4th Block", 12.9250, 77.5830, 6),
                "stop_07" to Stop("South End Circle", 12.9370, 77.5750, 7),
                "stop_08" to Stop("RV College of Engineering", 12.9237, 77.4987, 8)
            )
        ),
        "route_335e" to Route(
            routeNumber = "335E",
            name = "335E: Whitefield → PES University",
            college = "PES University",
            frequency = "Every 25 min",
            firstBus = "06:15 AM",
            lastBus = "08:00 PM",
            stops = mapOf(
                "stop_01" to Stop("Whitefield Bus Stand", 12.9698, 77.7500, 1),
                "stop_02" to Stop("ITPL Main Road", 12.9854, 77.7310, 2),
                "stop_03" to Stop("Marathahalli Bridge", 12.9591, 77.7019, 3),
                "stop_04" to Stop("KR Puram Railway", 12.9969, 77.6970, 4),
                "stop_05" to Stop("Tin Factory", 12.9935, 77.6620, 5),
                "stop_06" to Stop("Indiranagar", 12.9784, 77.6408, 6),
                "stop_07" to Stop("MG Road", 12.9756, 77.6095, 7),
                "stop_08" to Stop("PES University", 12.9344, 77.5350, 8)
            )
        ),
        "route_500d" to Route(
            routeNumber = "500D",
            name = "500D: Silk Board → Hebbal",
            college = "General / Multi-college",
            frequency = "Every 15 min",
            firstBus = "05:30 AM",
            lastBus = "10:00 PM",
            stops = mapOf(
                "stop_01" to Stop("Central Silk Board", 12.9176, 77.6238, 1),
                "stop_02" to Stop("HSR Layout", 12.9121, 77.6446, 2),
                "stop_03" to Stop("Agara Junction", 12.9234, 77.6501, 3),
                "stop_04" to Stop("Bellandur Gate", 12.9274, 77.6698, 4),
                "stop_05" to Stop("Marathahalli Bridge", 12.9553, 77.6984, 5),
                "stop_06" to Stop("Kalyan Nagar", 13.0280, 77.6399, 6),
                "stop_07" to Stop("Hebbal", 13.0354, 77.5988, 7)
            )
        )
    )

    // ── Seeding ─────────────────────────────────────────────────────────────

    /**
     * Seeds dummy BMTC routes if the routes node is empty.
     * Safe to call on every app start — checks before writing.
     */
    fun seedRoutesIfEmpty(onDone: () -> Unit = {}) {
        db.child("routes").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists() || snapshot.childrenCount == 0L) {
                    db.child("routes").setValue(getDummyRoutes())
                        .addOnCompleteListener { onDone() }
                } else {
                    onDone()
                }
            }
            override fun onCancelled(error: DatabaseError) { onDone() }
        })
    }

    // ── Student ──────────────────────────────────────────────────────────────
    
    suspend fun saveStudent(student: Student) {
        val uid = auth.currentUser?.uid ?: return
        try {
            db.child("students").child(uid).setValue(student).await()
        } catch (e: Exception) {
            android.util.Log.e("FirebaseRepo", "Error saving student", e)
            throw e
        }
    }

    suspend fun getStudent(): Student? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            val snapshot = db.child("students").child(uid).get().await()
            snapshot.getValue(Student::class.java)
        } catch (e: Exception) {
            android.util.Log.e("FirebaseRepo", "Error fetching student", e)
            null
        }
    }

    // ── Route ────────────────────────────────────────────────────────────────

    fun getRoute(routeId: String, onResult: (Route?) -> Unit) {
        // Try Firebase first; fall back to local dummy data
        db.child("routes").child(routeId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snap: DataSnapshot) {
                    val route = snap.getValue(Route::class.java)
                    if (route != null) {
                        onResult(route)
                    } else {
                        // Local fallback — works completely offline/no Firebase
                        onResult(getDummyRoutes()[routeId] ?: getDummyRoutes().values.firstOrNull())
                    }
                }
                override fun onCancelled(e: DatabaseError) {
                    onResult(getDummyRoutes()[routeId] ?: getDummyRoutes().values.firstOrNull())
                }
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
                    // Always include dummy routes as fallback
                    val result = if (map.isEmpty()) getDummyRoutes() else map
                    onResult(result)
                }
                override fun onCancelled(e: DatabaseError) { onResult(getDummyRoutes()) }
            })
    }

    suspend fun getAllRoutesAsync(): Map<String, Route> {
        return try {
            val snapshot = db.child("routes").get().await()
            val map = mutableMapOf<String, Route>()
            snapshot.children.forEach { child ->
                child.getValue(Route::class.java)?.let { map[child.key!!] = it }
            }
            if (map.isEmpty()) getDummyRoutes() else map
        } catch (e: Exception) {
            getDummyRoutes()
        }
    }

    // ── Ping ─────────────────────────────────────────────────────────────────

    fun pingBus(routeId: String, stopId: String) {
        val uid = auth.currentUser?.uid ?: "demo_user"
        val ping = BusPing(
            stopId    = stopId,
            timestamp = System.currentTimeMillis(),
            pinggedBy = uid,
            status    = "on_time"
        )
        db.child("pings").child(routeId).child("latest").setValue(ping)
            .addOnCompleteListener { task ->
                if (task.isSuccessful && uid != "demo_user") {
                    // Reward the student: +5 points for contributing live data
                    db.child("students").child(uid).child("contributionPoints")
                        .runTransaction(object : com.google.firebase.database.Transaction.Handler {
                            override fun doTransaction(data: com.google.firebase.database.MutableData): com.google.firebase.database.Transaction.Result {
                                val current = data.getValue(Int::class.java) ?: 0
                                data.value = current + 5
                                return com.google.firebase.database.Transaction.success(data)
                            }
                            override fun onComplete(e: com.google.firebase.database.DatabaseError?, b: Boolean, s: com.google.firebase.database.DataSnapshot?) {}
                        })
                }
            }
            .addOnFailureListener { e -> android.util.Log.e("FirebaseRepo", "Ping error", e) }
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

    // ── Breakdown ────────────────────────────────────────────────────────────

    fun reportBreakdown(routeId: String, message: String) {
        val uid = auth.currentUser?.uid ?: "demo_user"
        val breakdown = Breakdown(
            active     = true,
            reportedBy = uid,
            timestamp  = System.currentTimeMillis(),
            message    = message
        )
        db.child("breakdowns").child(routeId).setValue(breakdown)
    }

    fun clearBreakdown(routeId: String) {
        val uid = auth.currentUser?.uid ?: "demo_user"
        db.child("breakdowns").child(routeId).setValue(
            Breakdown(active = false, reportedBy = uid, timestamp = System.currentTimeMillis(), message = "")
        )
    }

    fun listenForBreakdown(routeId: String, onUpdate: (Breakdown?) -> Unit) {
        db.child("breakdowns").child(routeId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snap: DataSnapshot) { onUpdate(snap.getValue(Breakdown::class.java)) }
                override fun onCancelled(e: DatabaseError) { onUpdate(null) }
            })
    }

    // ── FCM Token ────────────────────────────────────────────────────────────

    fun updateFcmToken(token: String) {
        val uid = auth.currentUser?.uid ?: return
        db.child("students").child(uid).child("fcmToken").setValue(token)
    }

    // ── Safe Reach ───────────────────────────────────────────────────────────

    fun markSafeReach(studentName: String) {
        val uid = auth.currentUser?.uid ?: "demo_user"
        db.child("safereach").child(uid).setValue(
            mapOf(
                "studentName" to studentName,
                "timestamp"   to System.currentTimeMillis(),
                "reached"     to true
            )
        )
    }

    // ── Community Updates ────────────────────────────────────────────────────

    fun postRouteUpdate(routeId: String, studentName: String, message: String) {
        if (routeId.isEmpty()) return
        val ref = db.child("updates").child(routeId).push()
        val update = RouteUpdate(
            id = ref.key ?: "",
            studentName = studentName,
            message = message,
            timestamp = System.currentTimeMillis()
        )
        ref.setValue(update)
            .addOnFailureListener { e -> android.util.Log.e("FirebaseRepo", "Post failed", e) }
    }

    fun listenForRouteUpdates(routeId: String, onUpdate: (List<RouteUpdate>) -> Unit): ValueEventListener {
        val listener = object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {
                val list = mutableListOf<RouteUpdate>()
                if (snap.exists()) {
                    for (child in snap.children) {
                        try {
                            val update = child.getValue(RouteUpdate::class.java)
                            if (update != null) list.add(update)
                        } catch (e: Exception) {
                            android.util.Log.e("FirebaseRepo", "Serialization error", e)
                        }
                    }
                }
                onUpdate(list.sortedByDescending { it.timestamp }.take(10))
            }
            override fun onCancelled(e: DatabaseError) {}
        }
        db.child("updates").child(routeId).addValueEventListener(listener)
        return listener
    }

    fun removeRouteUpdatesListener(routeId: String, listener: ValueEventListener) {
        db.child("updates").child(routeId).removeEventListener(listener)
    }
}
