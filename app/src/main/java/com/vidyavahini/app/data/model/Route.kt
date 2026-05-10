package com.vidyavahini.app.data.model

/**
 * Represents a bus route with a name and map of stops.
 * Map key = stopId (e.g. "stop_01"), value = Stop object.
 */
data class Route(
    val name: String = "",
    val stops: Map<String, Stop> = emptyMap(),
    val frequency: String = "Every 15 min",
    val firstBus: String = "06:30 AM",
    val lastBus: String = "09:00 PM",
    val college: String = "",
    val routeNumber: String = ""
)
