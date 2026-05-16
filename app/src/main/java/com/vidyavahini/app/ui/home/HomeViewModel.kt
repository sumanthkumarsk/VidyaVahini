package com.vidyavahini.app.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.ValueEventListener
import com.vidyavahini.app.data.model.Breakdown
import com.vidyavahini.app.data.model.BusPing
import com.vidyavahini.app.data.model.Route
import com.vidyavahini.app.data.model.RouteUpdate
import com.vidyavahini.app.data.repository.FirebaseRepository
import com.vidyavahini.app.utils.ETACalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * HomeViewModel — powers the dashboard.
 * Handles real-time ping tracking, ETA calculation, and built-in demo simulation.
 * Demo simulation advances the bus one stop every [intervalMs] ms — no GPS needed.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: FirebaseRepository
) : ViewModel() {

    val latestPing    = MutableLiveData<BusPing?>()
    val etaText       = MutableLiveData<String>("Waiting for first ping...")
    val etaStatus     = MutableLiveData<String>("")
    val currentRoute  = MutableLiveData<Route>()
    val journeyProgress = MutableLiveData<Int>(0)
    val routeUpdates  = MutableLiveData<List<RouteUpdate>>(emptyList())
    val breakdown     = MutableLiveData<Breakdown?>()
    val isDemoRunning = MutableLiveData<Boolean>(false)
    val demoStopName  = MutableLiveData<String>("")

    private var pingListener: ValueEventListener? = null
    private var feedListener: ValueEventListener? = null
    private var activeRouteId  = ""
    private var studentStopOrd = 1
    private var demoJob: Job?  = null

    fun loadRoute(routeId: String) {
        activeRouteId = routeId
        repo.getRoute(routeId) { route ->
            route?.let { currentRoute.postValue(it) }
        }
    }

    fun startListening(routeId: String, studentStopOrder: Int) {
        activeRouteId  = routeId
        studentStopOrd = studentStopOrder
        
        pingListener = repo.listenForPings(routeId) { ping ->
            latestPing.postValue(ping)
            updateETA(ping)
        }
        
        feedListener = repo.listenForRouteUpdates(routeId) { updates ->
            routeUpdates.postValue(updates)
        }

        repo.listenForBreakdown(routeId) { b -> breakdown.postValue(b) }
    }

    private fun updateETA(ping: BusPing) {
        val stops    = currentRoute.value?.stops ?: return
        val busOrder = stops[ping.stopId]?.order ?: return
        val eta      = ETACalculator.calculateETA(activeRouteId, busOrder, studentStopOrd)
        
        etaText.postValue(ETACalculator.formatETA(eta))
        etaStatus.postValue(ETACalculator.getStatusBadge(eta))

        // Progress calculation: (Current Stop Order / Student Stop Order) * 100
        // Cap at 100%
        if (studentStopOrd > 0) {
            val progress = ((busOrder.toFloat() / studentStopOrd.toFloat()) * 100).toInt()
            journeyProgress.postValue(progress.coerceAtMost(100))
        }
    }

    fun pingBus(stopId: String) = repo.pingBus(activeRouteId, stopId)

    fun reportBreakdown(message: String) = repo.reportBreakdown(activeRouteId, message)

    fun clearBreakdown() = repo.clearBreakdown(activeRouteId)

    fun postUpdate(name: String, message: String) {
        if (message.isBlank()) return
        val routeId = if (activeRouteId.isNotEmpty()) activeRouteId else currentRoute.value?.routeNumber?.lowercase() ?: ""
        if (routeId.isNotEmpty()) {
            repo.postRouteUpdate(routeId, name, message)
        }
    }

    /**
     * Starts built-in bus simulator.
     * Iterates through ALL stops on the current route, pinging each one
     * after [intervalMs] ms delay. Works completely without GPS.
     * Perfect for demo presentations.
     */
    fun startDemoSimulation(intervalMs: Long = 120_000L) {
        if (isDemoRunning.value == true) {
            stopDemoSimulation()
            return
        }
        val route = currentRoute.value ?: return
        isDemoRunning.value = true
        val sortedStops = route.stops.entries.sortedBy { it.value.order }

        demoJob = viewModelScope.launch {
            for ((stopId, stop) in sortedStops) {
                demoStopName.postValue("🚌 Bus now at ${stop.name}")
                repo.pingBus(activeRouteId, stopId)
                delay(intervalMs)
            }
            demoStopName.postValue("🏁 Bus reached final destination!")
            isDemoRunning.postValue(false)
        }
    }

    fun stopDemoSimulation() {
        demoJob?.cancel()
        demoJob = null
        isDemoRunning.value = false
        demoStopName.value  = ""
    }

    override fun onCleared() {
        super.onCleared()
        pingListener?.let { repo.removePingListener(activeRouteId, it) }
        feedListener?.let { repo.removeRouteUpdatesListener(activeRouteId, it) }
        demoJob?.cancel()
    }
}
