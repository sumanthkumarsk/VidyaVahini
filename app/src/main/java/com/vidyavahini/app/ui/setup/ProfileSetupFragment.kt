package com.vidyavahini.app.ui.setup

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.vidyavahini.app.R
import com.vidyavahini.app.data.model.Student
import com.vidyavahini.app.databinding.FragmentProfileSetupBinding
import com.vidyavahini.app.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * ProfileSetupFragment — collects Name, College, Bus Route, Boarding Stop, Parent Phone.
 * Shown only after first sign-up.
 */
@AndroidEntryPoint
class ProfileSetupFragment : Fragment() {

    private var _binding: FragmentProfileSetupBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()

    // Map from route name (displayed) → route ID (stored)
    private var routeNameToId = mapOf<String, String>()
    // Map from stop name → stop ID (for the selected route)
    private var stopNameToId  = mapOf<String, String>()
    private var stopNameToOrder = mapOf<String, Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadRoutes()
        observeViewModel()
        setupCollegeDropdown()
        setupSaveButton()
    }

    private fun observeViewModel() {
        viewModel.routes.observe(viewLifecycleOwner) { routeMap ->
            if (routeMap.isEmpty()) return@observe

            routeNameToId = routeMap.entries.associate { it.value.name to it.key }
            val routeNames = routeMap.values.map { it.name }

            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, routeNames)
            binding.dropdownRoute.setAdapter(adapter)

            // When a route is selected, populate the stops dropdown
            binding.dropdownRoute.setOnItemClickListener { _, _, position, _ ->
                val selectedRouteName = routeNames[position]
                val selectedRouteId   = routeNameToId[selectedRouteName] ?: return@setOnItemClickListener
                val selectedRoute     = routeMap[selectedRouteId] ?: return@setOnItemClickListener
                val sortedStops       = selectedRoute.stops.entries.sortedBy { it.value.order }

                stopNameToId    = sortedStops.associate { "${it.value.order}. ${it.value.name}" to it.key }
                stopNameToOrder = sortedStops.associate { "${it.value.order}. ${it.value.name}" to it.value.order }

                val stopNames = sortedStops.map { "${it.value.order}. ${it.value.name}" }
                val stopAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, stopNames)
                binding.dropdownStop.setAdapter(stopAdapter)
                binding.dropdownStop.text?.clear()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.btnComplete.isEnabled = !loading
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.profileSaved.observe(viewLifecycleOwner) { saved ->
            if (saved) {
                findNavController().navigate(R.id.action_profileSetup_to_home)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { err ->
            if (err?.isNotEmpty() == true) {
                Toast.makeText(requireContext(), err, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupCollegeDropdown() {
        val colleges = listOf(
            "BMS College of Engineering",
            "RV College of Engineering",
            "PES University",
            "MSRIT",
            "PESIT South",
            "Dayananda Sagar College",
            "Other"
        )
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, colleges)
        binding.dropdownCollege.setAdapter(adapter)
    }

    private fun setupSaveButton() {
        binding.btnComplete.setOnClickListener {
            val name       = binding.etName.text.toString().trim()
            val college    = binding.dropdownCollege.text.toString().trim()
            val routeName  = binding.dropdownRoute.text.toString().trim()
            val stopLabel  = binding.dropdownStop.text.toString().trim()
            val parentPhone = binding.etParentPhone.text?.toString()?.trim() ?: ""

            when {
                name.isEmpty()     -> { binding.tilName.error = "Please enter your name"; return@setOnClickListener }
                college.isEmpty()  -> { Toast.makeText(requireContext(), "Please select your college", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                routeName.isEmpty() -> { Toast.makeText(requireContext(), "Please select your bus route", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                stopLabel.isEmpty() -> { Toast.makeText(requireContext(), "Please select your boarding stop", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            }

            binding.tilName.error = null

            val routeId   = routeNameToId[routeName] ?: ""
            val stopId    = stopNameToId[stopLabel] ?: ""
            val stopOrder = stopNameToOrder[stopLabel] ?: 1

            // Save locally for offline access + fast startup
            requireContext().getSharedPreferences("vidya", Context.MODE_PRIVATE).edit()
                .putString("name", name)
                .putString("college", college)
                .putString("routeId", routeId)
                .putString("stopId", stopId)
                .putInt("stopOrder", stopOrder)
                .putString("parentPhone", parentPhone)
                .apply()

            val student = Student(
                name            = name,
                college         = college,
                routeId         = routeId,
                stopId          = stopId,
                parentPhone     = parentPhone,
                profileComplete = true
            )
            viewModel.saveProfile(student)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
