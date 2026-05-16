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
import com.vidyavahini.app.ui.profile.RouteBottomSheetFragment
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

    // Selection storage
    private var selectedRouteId: String? = null
    private var selectedStopId: String? = null
    private var selectedStopOrder: Int = 1

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

            val openSelection = {
                val bottomSheet = RouteBottomSheetFragment(routeMap, object : RouteBottomSheetFragment.RouteSelectionListener {
                    override fun onRouteSelected(routeId: String, routeName: String, stopId: String, stopName: String, stopOrder: Int, college: String) {
                        selectedRouteId = routeId
                        selectedStopId = stopId
                        selectedStopOrder = stopOrder
                        
                        binding.dropdownRoute.setText(routeName, false)
                        binding.dropdownStop.setText("$stopOrder. $stopName", false)
                        
                        // Auto-fill college if not set
                        if (binding.dropdownCollege.text.isNullOrEmpty()) {
                            binding.dropdownCollege.setText(college, false)
                        }
                    }
                })
                bottomSheet.show(childFragmentManager, "RouteSelection")
            }

            binding.dropdownRoute.setOnClickListener { openSelection() }
            binding.dropdownStop.setOnClickListener { openSelection() }
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
                parentPhone.isNotEmpty() && parentPhone.length != 10 -> { Toast.makeText(requireContext(), "Parent phone must be 10 digits", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            }

            binding.tilName.error = null

            val routeId   = selectedRouteId ?: ""
            val stopId    = selectedStopId ?: ""
            val stopOrder = selectedStopOrder

            val formattedParentPhone = if (parentPhone.isEmpty()) "" else "+91$parentPhone"

            // Save locally for offline access + fast startup
            requireContext().getSharedPreferences("vidya", Context.MODE_PRIVATE).edit()
                .putString("name", name)
                .putString("college", college)
                .putString("routeId", routeId)
                .putString("stopId", stopId)
                .putInt("stopOrder", stopOrder)
                .putString("parentPhone", formattedParentPhone)
                .apply()

            val student = Student(
                name            = name,
                college         = college,
                routeId         = routeId,
                stopId          = stopId,
                parentPhone     = formattedParentPhone,
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
