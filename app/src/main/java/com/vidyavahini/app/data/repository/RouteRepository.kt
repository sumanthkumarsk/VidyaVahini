package com.vidyavahini.app.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.vidyavahini.app.data.model.Route
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RouteRepository @Inject constructor(
    private val db: FirebaseDatabase
) {
    private val routesRef = db.reference.child("routes")

    suspend fun getAllRoutes(): Map<String, Route> {
        val snapshot = routesRef.get().await()
        val map = mutableMapOf<String, Route>()
        snapshot.children.forEach { child ->
            child.getValue(Route::class.java)?.let { map[child.key!!] = it }
        }
        return map
    }

    suspend fun getRoute(routeId: String): Route? {
        val snapshot = routesRef.child(routeId).get().await()
        return snapshot.getValue(Route::class.java)
    }
}
