package com.vidyavahini.app.ui.tracking

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import com.vidyavahini.app.R
import com.vidyavahini.app.data.model.Route
import com.vidyavahini.app.databinding.FragmentTrackingBinding
import com.vidyavahini.app.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * TrackingFragment — the live map screen.
 * Shows full BMTC route polyline, stop markers, animated bus position, and ETA info.
 * Bus position updates automatically from HomeViewModel (real pings OR demo simulation).
 */
@AndroidEntryPoint
class TrackingFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentTrackingBinding? = null
    private val binding get() = _binding!!

    // Shared ViewModel — same instance as HomeFragment for live ping updates
    private val viewModel: HomeViewModel by viewModels()

    private var googleMap: GoogleMap? = null
    private var busMarker: Marker?    = null
    private var myStopId = ""
    private var routeId  = ""
    private var stopOrder = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs   = requireContext().getSharedPreferences("vidya", Context.MODE_PRIVATE)
        routeId     = prefs.getString("routeId", "route_401d") ?: "route_401d"
        myStopId    = prefs.getString("stopId", "stop_05") ?: "stop_05"
        stopOrder   = prefs.getInt("stopOrder", 5)

        // Initialize Google Maps
        (childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment)
            .getMapAsync(this)

        viewModel.loadRoute(routeId)
        viewModel.startListening(routeId, stopOrder)
        observeViewModel()

        // Ping button on map screen
        binding.btnPingMap.setOnClickListener {
            viewModel.pingBus(myStopId)
            val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.ping_pulse)
            binding.btnPingMap.startAnimation(anim)
            binding.btnPingMap.text      = "✅ Pinged!"
            binding.btnPingMap.isEnabled = false
            Snackbar.make(binding.root, "Bus pinged at your stop!", Snackbar.LENGTH_SHORT).show()
            binding.btnPingMap.postDelayed({
                _binding?.btnPingMap?.text      = "🚌 PING"
                _binding?.btnPingMap?.isEnabled = true
            }, 30_000L)
        }
    }

    private fun observeViewModel() {
        viewModel.etaText.observe(viewLifecycleOwner) { binding.tvEta.text = it }

        viewModel.latestPing.observe(viewLifecycleOwner) { ping ->
            if (ping == null) return@observe
            val map  = googleMap ?: return@observe
            val stop = viewModel.currentRoute.value?.stops?.get(ping.stopId) ?: return@observe

            // Smoothly move bus marker to the latest pinged stop
            busMarker?.remove()
            busMarker = map.addMarker(
                MarkerOptions()
                    .position(LatLng(stop.lat, stop.lng))
                    .title("🚌 Bus here: ${stop.name}")
                    .snippet("Stop ${stop.order} of ${viewModel.currentRoute.value?.stops?.size}")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                    .zIndex(3f)
            )
            busMarker?.showInfoWindow()
            binding.tvLastPing.text = "Bus at: ${stop.name}"

            // Auto-zoom to show bus + student's stop
            val myStop = viewModel.currentRoute.value?.stops?.get(myStopId)
            if (myStop != null) {
                val bounds = LatLngBounds.Builder()
                    .include(LatLng(stop.lat, stop.lng))
                    .include(LatLng(myStop.lat, myStop.lng))
                    .build()
                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200))
            }
        }

        viewModel.breakdown.observe(viewLifecycleOwner) { b ->
            binding.cardBreakdown.visibility = if (b?.active == true) View.VISIBLE else View.GONE
            binding.tvBreakdownMsg.text      = b?.message ?: ""
        }

        viewModel.currentRoute.observe(viewLifecycleOwner) { route ->
            googleMap?.let { map ->
                drawRouteLine(map, route, myStopId)
                centerCameraOnRoute(map, route)
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Apply dark map style
        try {
            val style = MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style_dark)
            map.setMapStyle(style)
        } catch (e: Exception) {
            Log.w("TrackingFragment", "Dark map style not loaded: ${e.message}")
        }

        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isCompassEnabled      = true
        map.uiSettings.isMapToolbarEnabled   = false
        map.mapType = GoogleMap.MAP_TYPE_NORMAL

        // Draw route if data already available
        viewModel.currentRoute.value?.let { route ->
            drawRouteLine(map, route, myStopId)
            centerCameraOnRoute(map, route)
        }
    }

    /**
     * Draws the route as a blue polyline with circular stop markers.
     * Student's stop = green highlight. Other stops = blue circles.
     */
    private fun drawRouteLine(map: GoogleMap, route: Route, studentStopId: String) {
        map.clear()
        busMarker = null

        val sortedStops = route.stops.entries.sortedBy { it.value.order }
        val latLngs     = sortedStops.map { LatLng(it.value.lat, it.value.lng) }
        if (latLngs.size < 2) return

        // Route polyline — blue gradient
        map.addPolyline(
            PolylineOptions()
                .addAll(latLngs)
                .color(Color.parseColor("#5E92F3"))
                .width(14f)
                .geodesic(true)
        )

        // Stop markers
        sortedStops.forEach { (stopId, stop) ->
            val isMyStop = (stopId == studentStopId)

            // Filled circle at each stop
            map.addCircle(
                CircleOptions()
                    .center(LatLng(stop.lat, stop.lng))
                    .radius(120.0)
                    .fillColor(
                        if (isMyStop) Color.parseColor("#8000C853")  // green for my stop
                        else Color.parseColor("#805E92F3")            // blue for others
                    )
                    .strokeColor(
                        if (isMyStop) Color.parseColor("#00C853")
                        else Color.parseColor("#5E92F3")
                    )
                    .strokeWidth(5f)
                    .zIndex(1f)
            )

            // Pin marker with stop number
            map.addMarker(
                MarkerOptions()
                    .position(LatLng(stop.lat, stop.lng))
                    .title("Stop ${stop.order}: ${stop.name}${if (isMyStop) " ★ YOUR STOP" else ""}")
                    .snippet("Tap for stop details")
                    .icon(
                        BitmapDescriptorFactory.defaultMarker(
                            if (isMyStop) BitmapDescriptorFactory.HUE_GREEN
                            else BitmapDescriptorFactory.HUE_AZURE
                        )
                    )
                    .zIndex(2f)
            )
        }

        // Re-add bus marker at last known position
        viewModel.latestPing.value?.let { ping ->
            val stop = route.stops[ping.stopId] ?: return@let
            busMarker = map.addMarker(
                MarkerOptions()
                    .position(LatLng(stop.lat, stop.lng))
                    .title("🚌 Bus here: ${stop.name}")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                    .zIndex(3f)
            )
        }
    }

    private fun centerCameraOnRoute(map: GoogleMap, route: Route) {
        val stops = route.stops.values.toList()
        if (stops.isEmpty()) return
        val builder = LatLngBounds.Builder()
        stops.forEach { builder.include(LatLng(it.lat, it.lng)) }
        try {
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 150))
        } catch (_: Exception) {}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        googleMap = null
        _binding  = null
    }
}
