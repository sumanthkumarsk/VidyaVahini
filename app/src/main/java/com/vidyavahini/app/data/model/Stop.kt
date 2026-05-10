package com.vidyavahini.app.data.model

import com.google.firebase.database.PropertyName

/**
 * Represents a single bus stop on a route.
 * Default values are REQUIRED for Firebase deserialization.
 */
data class Stop(
    val name: String = "",
    @get:PropertyName("lat") @set:PropertyName("lat")
    var lat: Double = 0.0,
    @get:PropertyName("lng") @set:PropertyName("lng")
    var lng: Double = 0.0,
    val order: Int = 0
) {
    // Compatibility with JSON sources using 'latitude'/'longitude'
    @get:PropertyName("latitude") @set:PropertyName("latitude")
    var latitude: Double
        get() = lat
        set(value) { lat = value }

    @get:PropertyName("longitude") @set:PropertyName("longitude")
    var longitude: Double
        get() = lng
        set(value) { lng = value }
}
