package com.vidyavahini.app.ui.issues

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.vidyavahini.app.R
import com.vidyavahini.app.databinding.FragmentReportIssueBinding
import com.vidyavahini.app.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * ReportIssueFragment — broadcasts bus issues to all students on the route.
 */
@AndroidEntryPoint
class ReportIssueFragment : Fragment() {

    private var _binding: FragmentReportIssueBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private var selectedIssueType = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportIssueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        binding.root.startAnimation(fadeIn)

        val prefs   = requireContext().getSharedPreferences("vidya", Context.MODE_PRIVATE)
        val routeId = prefs.getString("routeId", "route_401d") ?: "route_401d"

        viewModel.loadRoute(routeId)
        viewModel.startListening(routeId, 1)

        setupIssueTypeButtons()

        binding.btnSubmitIssue.setOnClickListener {
            if (selectedIssueType.isEmpty()) {
                Snackbar.make(binding.root, "Please select an issue type first", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val note    = binding.etCustomNote.text.toString().trim()
            val message = buildMessage(selectedIssueType, note)
            viewModel.reportBreakdown(message)
            showSuccess(message)
        }

        binding.btnClearAlert.setOnClickListener {
            viewModel.clearBreakdown()
            Snackbar.make(binding.root, "Alert cleared for your route.", Snackbar.LENGTH_SHORT).show()
            resetForm()
        }

        observeViewModel()
    }

    private fun buildMessage(type: String, note: String): String {
        val base = when (type) {
            "BREAKDOWN"   -> "🔧 Bus Breakdown reported! Please find alternatives."
            "HEAVY_DELAY" -> "⏰ Heavy delay (30+ min) — bus is running very late."
            "DIVERTED"    -> "🔄 Route diverted — check alternate stops."
            "CANCELLED"   -> "❌ Bus service cancelled for this route today."
            else          -> type
        }
        return if (note.isNotEmpty()) "$base\n📌 $note" else base
    }

    private fun setupIssueTypeButtons() {
        val buttonMap = mapOf(
            binding.btnBreakdown to "BREAKDOWN",
            binding.btnDelay     to "HEAVY_DELAY",
            binding.btnDiverted  to "DIVERTED",
            binding.btnCancelled to "CANCELLED"
        )

        buttonMap.forEach { (button, type) ->
            button.setOnClickListener {
                selectedIssueType = type
                // Reset all, highlight selected
                buttonMap.keys.forEach { b -> b.isSelected = false; b.alpha = 0.65f }
                button.isSelected = true
                button.alpha = 1f
                button.animate().scaleX(1.06f).scaleY(1.06f).setDuration(120)
                    .withEndAction { button.animate().scaleX(1f).scaleY(1f).setDuration(120).start() }
                    .start()
            }
        }
    }

    private fun showSuccess(message: String) {
        binding.cardSuccess.visibility = View.VISIBLE
        binding.cardSuccess.alpha = 0f
        binding.cardSuccess.animate().alpha(1f).setDuration(400).start()
        binding.tvSuccessMsg.text =
            "⚠️ Alert broadcast to ALL students on your route!\n\nThey can see this alert immediately."
        val bounce = AnimationUtils.loadAnimation(requireContext(), R.anim.bounce_scale)
        binding.ivSuccessIcon.startAnimation(bounce)
        Snackbar.make(binding.root, "Issue reported! All students on your route notified.", Snackbar.LENGTH_LONG).show()
    }

    private fun resetForm() {
        binding.cardSuccess.visibility = View.GONE
        selectedIssueType = ""
        binding.etCustomNote.text?.clear()
        listOf(binding.btnBreakdown, binding.btnDelay, binding.btnDiverted, binding.btnCancelled)
            .forEach { it.isSelected = false; it.alpha = 0.65f }
    }

    private fun observeViewModel() {
        viewModel.breakdown.observe(viewLifecycleOwner) { b ->
            if (b?.active == true) {
                binding.tvActiveAlert.text      = b.message
                binding.tvActiveAlert.visibility = View.VISIBLE
                binding.btnClearAlert.visibility = View.VISIBLE
            } else {
                binding.tvActiveAlert.visibility = View.GONE
                binding.btnClearAlert.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
