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
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.vidyavahini.app.R
import com.vidyavahini.app.databinding.FragmentHomeBinding
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vidyavahini.app.data.model.RouteUpdate
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
    
    // Community Feed Adapter
    private val feedUpdates = mutableListOf<RouteUpdate>()
    private val feedAdapter by lazy {
        object : RecyclerView.Adapter<UpdateViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpdateViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_route_update, parent, false)
                return UpdateViewHolder(view)
            }

            override fun onBindViewHolder(holder: UpdateViewHolder, position: Int) {
                val item = feedUpdates[position]
                holder.author.text = item.studentName
                holder.message.text = item.message
                
                val diff = System.currentTimeMillis() - item.timestamp
                holder.time.text = when {
                    diff < 60_000 -> "Just now"
                    diff < 3600_000 -> "${diff / 60_000}m ago"
                    else -> "${diff / 3600_000}h ago"
                }
            }

            override fun getItemCount() = feedUpdates.size
        }
    }

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
        setupFeedActions()
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

        // Demo button removed from UI, automated instead
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

    private fun setupFeedActions() {
        binding.rvUpdates.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUpdates.adapter = feedAdapter
        
        binding.btnPostUpdate.setOnClickListener { postCurrentUpdate() }
        
        binding.etUpdateMessage.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                postCurrentUpdate()
                true
            } else false
        }
    }

    private fun postCurrentUpdate() {
        val msg = binding.etUpdateMessage.text.toString().trim()
        if (msg.isNotEmpty()) {
            val prefs = requireContext().getSharedPreferences("vidya", Context.MODE_PRIVATE)
            val name  = prefs.getString("name", "Student") ?: "Student"
            
            // Local Echo: Add to list immediately for testing UI
            val localUpdate = RouteUpdate(
                id = "local_" + System.currentTimeMillis(),
                studentName = name,
                message = msg,
                timestamp = System.currentTimeMillis()
            )
            
            // Add at top
            feedUpdates.add(0, localUpdate)
            if (feedUpdates.size > 3) feedUpdates.removeAt(3)
            
            binding.tvNoUpdates.visibility = View.GONE
            binding.rvUpdates.visibility = View.VISIBLE
            feedAdapter.notifyDataSetChanged()
            binding.rvUpdates.scrollToPosition(0)

            // Clear UI
            binding.etUpdateMessage.text?.clear()
            val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.hideSoftInputFromWindow(binding.etUpdateMessage.windowToken, 0)
            
            viewModel.postUpdate(name, msg)
            Snackbar.make(binding.root, "Update shared with community", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModel() {
        viewModel.currentRoute.observe(viewLifecycleOwner) { route ->
            binding.tvRouteName.text  = route.name
            binding.tvTotalStops.text = route.stops.size.toString()
            binding.tvMyStopNum.text  = stopOrder.toString()
            binding.tvFrequency.text  = route.frequency.replace("Every ", "")

            // Automatically start simulation if not already running
            if (viewModel.isDemoRunning.value != true) {
                viewModel.startDemoSimulation()
            }
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

        viewModel.journeyProgress.observe(viewLifecycleOwner) { progress ->
            if (progress > 0) {
                binding.progressJourney.visibility = View.VISIBLE
                binding.tvProgressLabel.visibility = View.VISIBLE
                binding.progressJourney.setProgress(progress, true)
                
                binding.tvProgressLabel.text = when {
                    progress >= 100 -> "🏁 Bus has reached your stop!"
                    progress > 80   -> "🚨 Almost there! Get ready to board."
                    progress > 50   -> "🚌 Halfway to your stop..."
                    else            -> "🛣️ Bus is on the way..."
                }
            } else {
                binding.progressJourney.visibility = View.GONE
                binding.tvProgressLabel.visibility = View.GONE
            }
        }

        viewModel.latestPing.observe(viewLifecycleOwner) { ping ->
            if (ping == null) return@observe
            val stopName = viewModel.currentRoute.value?.stops?.get(ping.stopId)?.name ?: ping.stopId
            val timeStr  = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(ping.timestamp))
            binding.tvLastPing.text     = stopName
            binding.tvLastPingTime.text = "$timeStr • ${getTimeAgo(ping.timestamp)}"
            binding.pingCard.visibility = View.VISIBLE
        }

        viewModel.routeUpdates.observe(viewLifecycleOwner) { updates ->
            if (updates.isNullOrEmpty()) {
                binding.tvNoUpdates.visibility = View.VISIBLE
                binding.rvUpdates.visibility = View.GONE
                feedUpdates.clear()
                feedAdapter.notifyDataSetChanged()
            } else {
                binding.tvNoUpdates.visibility = View.GONE
                binding.rvUpdates.visibility = View.VISIBLE
                
                feedUpdates.clear()
                feedUpdates.addAll(updates)
                feedAdapter.notifyDataSetChanged()
                
                // Force a layout pass and scroll to newest
                binding.rvUpdates.post {
                    binding.rvUpdates.requestLayout()
                    binding.rvUpdates.scrollToPosition(0)
                }
            }
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
            // Keep demo buttons hidden as per user request
            binding.btnStartDemo.visibility = View.GONE
            binding.btnStopDemo.visibility  = View.GONE
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

    class UpdateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val author: TextView = view.findViewById(R.id.tvUpdateAuthor)
        val time: TextView = view.findViewById(R.id.tvUpdateTime)
        val message: TextView = view.findViewById(R.id.tvUpdateMessage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cooldownTimer?.cancel()
        _binding = null
    }
}
