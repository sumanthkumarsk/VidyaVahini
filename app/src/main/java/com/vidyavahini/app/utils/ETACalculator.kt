package com.vidyavahini.app.utils

/**
 * Pure-Kotlin ETA calculator — no Firebase, no Android dependencies.
 * Uses average travel time between stops to compute arrival estimate.
 */
object ETACalculator {

    /** Average minutes per stop for each route. Add new routes here as needed. */
    private val avgMinutesPerStop = mapOf(
        "route_401d"   to 7,
        "route_500ca"  to 9,
        "route_335e"   to 10,
        "route_500d"   to 8,
        "route_kia8"   to 15,
        "route_default" to 8
    )

    /**
     * Calculates ETA in minutes.
     * @param routeId           Route identifier
     * @param busCurrentOrder   Stop order where bus was last pinged
     * @param studentStopOrder  Student's boarding stop order
     * @return Minutes until bus arrives (0 if bus has already passed)
     */
    fun calculateETA(routeId: String, busCurrentOrder: Int, studentStopOrder: Int): Int {
        val avg       = avgMinutesPerStop[routeId] ?: avgMinutesPerStop["route_default"]!!
        val stopsAway = studentStopOrder - busCurrentOrder
        return if (stopsAway > 0) stopsAway * avg else 0
    }

    /** Converts raw minutes into a user-friendly message. */
    fun formatETA(minutes: Int): String = when {
        minutes <= 0  -> "🚌 Bus may have already passed your stop"
        minutes <= 2  -> "🚨 Bus arriving NOW — head to your stop!"
        minutes <= 5  -> "⚡ Bus in $minutes min — get ready!"
        minutes <= 15 -> "⏱️ Bus expected in $minutes minutes"
        minutes <= 60 -> "🕐 Bus expected in $minutes minutes"
        else          -> "🕐 Bus expected in ${minutes / 60}h ${minutes % 60}m"
    }

    /** Returns a short badge for the ETA chip. */
    fun getStatusBadge(minutes: Int): String = when {
        minutes <= 0  -> "PASSED"
        minutes <= 5  -> "ARRIVING"
        minutes <= 15 -> "SOON"
        else          -> "ON TIME"
    }
}
