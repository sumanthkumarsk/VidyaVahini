package com.vidyavahini.app

import android.app.Application
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class — initialises Firebase offline persistence.
 * Offline persistence caches data for 2G/rural connectivity.
 * Must be registered in AndroidManifest.xml as android:name=".MyApplication"
 */
@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            FirebaseDatabase.getInstance(
                "https://vidya-vahini-20c3d-default-rtdb.asia-southeast1.firebasedatabase.app"
            ).setPersistenceEnabled(true)
        } catch (e: Exception) {
            Log.e("MyApplication", "Firebase persistence init failed: ${e.message}")
        }
    }
}
