package com.vidyavahini.app.data.model

/**
 * A short community update about a specific route.
 * Crowdsourced from students to share traffic or crowd info.
 */
data class RouteUpdate(
    val id: String = "",
    val studentName: String = "",
    val message: String = "",
    val timestamp: Long = 0L
)
