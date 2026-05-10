package com.vidyavahini.app.ui.profile

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.vidyavahini.app.R
import com.vidyavahini.app.data.model.Student
import com.vidyavahini.app.databinding.FragmentProfileBinding
import com.vidyavahini.app.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * ProfileFragment — user settings, route switcher, dark mode, sign out.
 */
@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var prefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = requireContext().getSharedPreferences("vidya", Context.MODE_PRIVATE)
        loadProfileData()
        setupUI()
        observeViewModel()
    }

    private fun loadProfileData() {
        val name    = prefs.getString("name", "Student") ?: "Student"
        val college = prefs.getString("college", "Not set") ?: "Not set"
        val email   = FirebaseAuth.getInstance().currentUser?.email ?: "Not signed in"

        binding.tvProfileName.text  = name
        binding.tvProfileEmail.text = email
        binding.tvCollege.text      = college
        binding.etEditName.setText(name)
        binding.etParentPhone.setText(prefs.getString("parentPhone", ""))

        // Set avatar initial
        binding.tvAvatarInitial.text = name.firstOrNull()?.uppercaseChar()?.toString() ?: "S"

        // Dark mode toggle
        val isDark = prefs.getBoolean("darkMode", false)
        binding.switchDarkMode.isChecked = isDark

        viewModel.loadRoutes()
    }

    private fun setupUI() {
        // Dark mode toggle
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
            prefs.edit().putBoolean("darkMode", isChecked).apply()
        }

        // Save profile
        binding.btnSaveProfile.setOnClickListener {
            val newName  = binding.etEditName.text.toString().trim()
            val newPhone = binding.etParentPhone.text.toString().trim()
            if (newName.isEmpty()) {
                Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            prefs.edit().putString("name", newName).putString("parentPhone", newPhone).apply()
            binding.tvProfileName.text = newName
            binding.tvAvatarInitial.text = newName.firstOrNull()?.uppercaseChar()?.toString() ?: "S"

            val student = Student(
                name = newName,
                college = prefs.getString("college", "") ?: "",
                routeId = prefs.getString("routeId", "") ?: "",
                stopId  = prefs.getString("stopId", "") ?: "",
                parentPhone = newPhone,
                profileComplete = true
            )
            viewModel.saveProfile(student)
            Snackbar.make(binding.root, "✅ Profile updated!", Snackbar.LENGTH_SHORT).show()
        }

        // Switch route
        binding.btnSwitchRoute.setOnClickListener {
            val routes = viewModel.routes.value
            if (routes.isNullOrEmpty()) {
                Snackbar.make(binding.root, "Loading routes…", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val ids   = routes.keys.toList()
            val names = routes.values.map { it.name }.toTypedArray()
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Switch Route")
                .setItems(names) { _, idx ->
                    val newId   = ids[idx]
                    val route   = routes[newId]!!
                    val first   = route.stops.entries.minByOrNull { it.value.order }
                    prefs.edit()
                        .putString("routeId", newId)
                        .putString("stopId", first?.key ?: "")
                        .putInt("stopOrder", first?.value?.order ?: 1)
                        .putString("college", route.college)
                        .apply()
                    binding.tvCollege.text = route.name
                    Snackbar.make(binding.root, "Route switched to ${route.name}", Snackbar.LENGTH_LONG).show()
                }.show()
        }

        // Sign out
        binding.btnSignOut.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton("Sign Out") { _, _ ->
                    FirebaseAuth.getInstance().signOut()
                    prefs.edit().clear().apply()
                    findNavController().navigate(R.id.action_profile_to_onboarding)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // About
        binding.btnAbout.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("About Vidya-Vahini")
                .setMessage(
                    "🚌 Vidya-Vahini v1.0\n\n" +
                    "Real-time crowdsourced BMTC bus tracking for Bangalore students.\n\n" +
                    "Built for MindMatrix Internship — Project #101\n\n" +
                    "Features:\n" +
                    "• Real-time crowdsourced tracking\n" +
                    "• Live map with bus position\n" +
                    "• Safe reach parent notifications\n" +
                    "• Demo simulation mode\n" +
                    "• Offline-first with Firebase cache"
                )
                .setPositiveButton("OK", null)
                .show()
        }
    }

    private fun observeViewModel() {
        viewModel.profileSaved.observe(viewLifecycleOwner) { saved ->
            if (saved) Snackbar.make(binding.root, "Profile saved to cloud!", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
