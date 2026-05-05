package com.vidyavahini.app.ui.home

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.vidyavahini.app.R
import com.vidyavahini.app.databinding.FragmentHomeBinding
import com.vidyavahini.app.viewmodel.TrackingViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import dagger.hilt.android.AndroidEntryPoint

/**
 * HomeFragment — the primary dashboard.
 * Shows: route name, last ping info, ETA, PING button, breakdown button, nav actions.
 * This is the screen students use 90% of the time.
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TrackingViewModel by viewModels()

    private var routeId   = ""
    private var stopId    = ""
    private var stopOrder = 1
    private var isPingCooldown = false
    private var cooldownTimer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fade-in animation for polished entrance
        val fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        binding.root.startAnimation(fadeIn)

        loadPreferences()
        setupUI()
        observeViewModel()
        startTracking()
    }

    private fun loadPreferences() {
        val prefs   = requireContext().getSharedPreferences("vidya", Context.MODE_PRIVATE)
        routeId     = prefs.getString("routeId", "") ?: ""
        stopId      = prefs.getString("stopId", "") ?: ""
        stopOrder   = prefs.getInt("stopOrder", 1)
        val name    = prefs.getString("name", "Student") ?: "Student"

        binding.tvGreeting.text  = "Hello, $name 👋"
        // Show a placeholder route name until Firebase data loads
        binding.tvRouteName.text = "Loading route..."
    }

    private fun setupUI() {
        // ── PING button — core action ──────────────────────────────────────
        binding.btnPing.setOnClickListener {
            if (isPingCooldown) return@setOnClickListener
            triggerPing()
        }

        // ── Report Breakdown ───────────────────────────────────────────────
        binding.btnBreakdown.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("⚠️ Report Bus Breakdown?")
                .setMessage("This will alert ALL students on your route to find alternatives.")
                .setPositiveButton("Yes, Report") { _, _ ->
                    viewModel.reportBreakdown("Bus has broken down — please find alternatives!")
                    Snackbar.make(binding.root, "Breakdown reported. All students notified.", Snackbar.LENGTH_LONG).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // ── Navigate to Map ────────────────────────────────────────────────
        binding.btnMap.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_tracking)
        }

        // ── Navigate to Safe Reach ─────────────────────────────────────────
        binding.btnSafeReach.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_safe_reach)
        }

        // ── Logout ─────────────────────────────────────────────────────────
        binding.btnLogout.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton("Sign Out") { _, _ ->
                    FirebaseAuth.getInstance().signOut()
                    requireContext().getSharedPreferences("vidya", Context.MODE_PRIVATE)
                        .edit().clear().apply()
                    // Navigate back to login, clearing the back stack
                    findNavController().navigate(R.id.action_home_to_login)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // ── Hidden Demo Simulator ──────────────────────────────────────────
        binding.tvRouteName.setOnLongClickListener {
            val route = viewModel.currentRoute.value
            if (route == null) {
                Toast.makeText(requireContext(), "Route not loaded yet", Toast.LENGTH_SHORT).show()
                return@setOnLongClickListener true
            }

            Toast.makeText(requireContext(), "Starting Live Demo Simulation...", Toast.LENGTH_LONG).show()

            val sortedStops = route.stops.entries.sortedBy { it.value.order }

            viewLifecycleOwner.lifecycleScope.launch {
                for ((sid, stop) in sortedStops) {
                    // Send ping for this stop
                    viewModel.pingBus(sid)
                    
                    Snackbar.make(binding.root, "Demo: Bus reached ${stop.name}", Snackbar.LENGTH_SHORT).show()
                    
                    // Wait 5 seconds before moving to next stop
                    delay(5000)
                }
                Snackbar.make(binding.root, "Demo: Bus reached final destination!", Snackbar.LENGTH_LONG).show()
            }
            true
        }
    }

    private fun triggerPing() {
        isPingCooldown = true
        viewModel.pingBus(stopId)

        // Haptic feedback — makes the action feel impactful
        binding.btnPing.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        try {
            val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        } catch (_: Exception) { /* vibrator not available */ }

        // Animate the ping button
        val pulseAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.ping_pulse)
        binding.btnPing.startAnimation(pulseAnim)
        binding.btnPing.isEnabled = false

        // Show feedback snackbar
        Snackbar.make(binding.root, "Bus ping sent! All students on your route notified.", Snackbar.LENGTH_SHORT).show()

        // 2-minute countdown timer with live display
        cooldownTimer?.cancel()
        cooldownTimer = object : CountDownTimer(120_000L, 1_000L) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished / 1000).toInt()
                val min = seconds / 60
                val sec = seconds % 60
                _binding?.btnPing?.text = "⏳ Wait ${min}:${String.format("%02d", sec)}"
            }
            override fun onFinish() {
                isPingCooldown = false
                _binding?.btnPing?.text      = "🚌  PING BUS"
                _binding?.btnPing?.isEnabled = true
            }
        }.start()
    }

    private fun observeViewModel() {
        viewModel.etaText.observe(viewLifecycleOwner) { eta ->
            binding.tvEta.text = eta
        }

        viewModel.etaStatus.observe(viewLifecycleOwner) { status ->
            binding.chipStatus.text = status
            val color = when (status) {
                "ARRIVING" -> R.color.status_arriving
                "SOON"     -> R.color.status_soon
                "PASSED"   -> R.color.status_passed
                else       -> R.color.status_on_time
            }
            binding.chipStatus.setChipBackgroundColorResource(color)
        }

        viewModel.latestPing.observe(viewLifecycleOwner) { ping ->
            val stopName = viewModel.currentRoute.value?.stops?.get(ping.stopId)?.name ?: ping.stopId
            val timeStr  = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(ping.timestamp))
            val ago      = getTimeAgo(ping.timestamp)
            binding.tvLastPing.text      = "Last seen at: $stopName"
            binding.tvLastPingTime.text  = "$timeStr  •  $ago"
            binding.pingCard.visibility  = View.VISIBLE
        }

        viewModel.breakdown.observe(viewLifecycleOwner) { b ->
            if (b?.active == true) {
                binding.breakdownCard.visibility = View.VISIBLE
                binding.tvBreakdownMsg.text      = b.message
            } else {
                binding.breakdownCard.visibility = View.GONE
            }
        }

        // Use the real route name from Firebase once it loads
        viewModel.currentRoute.observe(viewLifecycleOwner) { route ->
            binding.tvRouteName.text = route.name
        }
    }

    /**
     * Returns a human-readable "X min ago" / "X sec ago" string.
     */
    private fun getTimeAgo(timestamp: Long): String {
        val diff = System.currentTimeMillis() - timestamp
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours   = minutes / 60
        return when {
            seconds < 60 -> "just now"
            minutes < 60 -> "${minutes}m ago"
            hours < 24   -> "${hours}h ago"
            else         -> "${hours / 24}d ago"
        }
    }

    private fun startTracking() {
        if (routeId.isEmpty()) return
        viewModel.loadRoute(routeId)
        viewModel.startListening(routeId, stopOrder)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cooldownTimer?.cancel()
        _binding = null
    }
}
