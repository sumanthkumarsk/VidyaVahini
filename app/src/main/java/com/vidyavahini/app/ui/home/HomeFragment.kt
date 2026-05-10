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
import com.vidyavahini.app.R
import com.vidyavahini.app.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

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

        val fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        binding.root.startAnimation(fadeIn)

        loadPreferences()
        setupUI()
        observeViewModel()
        startTracking()
    }

    private fun loadPreferences() {
        val prefs = requireContext().getSharedPreferences("vidya", Context.MODE_PRIVATE)
        routeId   = prefs.getString("routeId", "route_401d") ?: "route_401d"
        stopId    = prefs.getString("stopId", "stop_05") ?: "stop_05"
        stopOrder = prefs.getInt("stopOrder", 5)
        val name  = prefs.getString("name", "Student") ?: "Student"

        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when {
            hour < 12 -> "Good Morning"
            hour < 17 -> "Good Afternoon"
            else      -> "Good Evening"
        }
        binding.tvGreeting.text = "$greeting, $name 👋"
    }

    private fun setupUI() {
        binding.swipeRefresh.setColorSchemeResources(R.color.primary_blue, R.color.accent_amber)
        binding.swipeRefresh.setOnRefreshListener {
            startTracking()
            binding.swipeRefresh.postDelayed({ binding.swipeRefresh.isRefreshing = false }, 1500)
        }

        // PING BUS
        binding.btnPing.setOnClickListener {
            if (isPingCooldown) {
                Snackbar.make(binding.root, "Please wait for cooldown to finish.", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            triggerPing()
        }

        // Safe Reach
        binding.btnSafeReach.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_safe_reach)
        }

        // Start Demo
        binding.btnStartDemo.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("🚌 Start Demo Mode")
                .setMessage("Demo mode will automatically move the bus through all stops on your route — no GPS needed. Great for presentations!\n\nThe bus advances every 8 seconds.")
                .setPositiveButton("Start Demo") { _, _ ->
                    viewModel.startDemoSimulation(8000L)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // Stop Demo
        binding.btnStopDemo.setOnClickListener {
            viewModel.stopDemoSimulation()
            Snackbar.make(binding.root, "Demo simulation stopped.", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun triggerPing() {
        isPingCooldown = true
        viewModel.pingBus(stopId)

        // Haptic feedback
        binding.btnPing.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        try {
            @Suppress("DEPRECATION")
            val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        } catch (_: Exception) {}

        // Pulse animation
        val pulseAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.ping_pulse)
        binding.btnPing.startAnimation(pulseAnim)
        binding.btnPing.isEnabled = false

        Snackbar.make(binding.root, "✅ Bus ping sent! Your route students are notified.", Snackbar.LENGTH_SHORT).show()

        // 2-min cooldown
        cooldownTimer?.cancel()
        cooldownTimer = object : CountDownTimer(120_000L, 1_000L) {
            override fun onTick(millisUntilFinished: Long) {
                val s = (millisUntilFinished / 1000).toInt()
                _binding?.btnPing?.text = "⏳ ${s / 60}:${String.format("%02d", s % 60)}"
            }
            override fun onFinish() {
                isPingCooldown = false
                _binding?.btnPing?.text = "🚌  PING BUS"
                _binding?.btnPing?.isEnabled = true
            }
        }.start()
    }

    private fun observeViewModel() {
        viewModel.currentRoute.observe(viewLifecycleOwner) { route ->
            binding.tvRouteName.text  = route.name
            binding.tvTotalStops.text = route.stops.size.toString()
            binding.tvMyStopNum.text  = stopOrder.toString()
            binding.tvFrequency.text  = route.frequency.replace("Every ", "")
        }

        viewModel.etaText.observe(viewLifecycleOwner) { binding.tvEta.text = it }

        viewModel.etaStatus.observe(viewLifecycleOwner) { status ->
            binding.chipStatus.text = status.ifEmpty { "ON TIME" }
            val colorRes = when (status) {
                "ARRIVING" -> R.color.status_arriving
                "SOON"     -> R.color.status_soon
                "PASSED"   -> R.color.status_passed
                else       -> R.color.status_on_time
            }
            binding.chipStatus.setChipBackgroundColorResource(colorRes)
        }

        viewModel.latestPing.observe(viewLifecycleOwner) { ping ->
            if (ping == null) return@observe
            val stopName = viewModel.currentRoute.value?.stops?.get(ping.stopId)?.name ?: ping.stopId
            val timeStr  = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(ping.timestamp))
            binding.tvLastPing.text     = stopName
            binding.tvLastPingTime.text = "$timeStr • ${getTimeAgo(ping.timestamp)}"
            binding.pingCard.visibility = View.VISIBLE
        }

        viewModel.breakdown.observe(viewLifecycleOwner) { b ->
            if (b?.active == true) {
                binding.breakdownCard.visibility = View.VISIBLE
                binding.tvBreakdownMsg.text      = b.message
            } else {
                binding.breakdownCard.visibility = View.GONE
            }
        }

        viewModel.isDemoRunning.observe(viewLifecycleOwner) { running ->
            binding.chipDemoMode.visibility = if (running) View.VISIBLE else View.GONE
            binding.tvDemoStatus.visibility = if (running) View.VISIBLE else View.GONE
            binding.btnStartDemo.visibility = if (running) View.GONE else View.VISIBLE
            binding.btnStopDemo.visibility  = if (running) View.VISIBLE else View.GONE
        }

        viewModel.demoStopName.observe(viewLifecycleOwner) { msg ->
            if (msg.isNotEmpty()) binding.tvDemoStatus.text = msg
        }
    }

    private fun getTimeAgo(timestamp: Long): String {
        val diff    = System.currentTimeMillis() - timestamp
        val seconds = diff / 1000
        val minutes = seconds / 60
        return when {
            seconds < 60 -> "just now"
            minutes < 60 -> "${minutes}m ago"
            else         -> "${minutes / 60}h ago"
        }
    }

    private fun startTracking() {
        // Default to 401D if no prefs set (new user, demo mode)
        if (routeId.isEmpty()) routeId = "route_401d"
        viewModel.loadRoute(routeId)
        viewModel.startListening(routeId, stopOrder)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cooldownTimer?.cancel()
        _binding = null
    }
}
