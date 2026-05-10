package com.vidyavahini.app

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.vidyavahini.app.data.repository.FirebaseRepository
import com.vidyavahini.app.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Single-activity host. Manages splash, bottom nav, auth routing, and route seeding.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    @Inject
    lateinit var firebaseRepository: FirebaseRepository

    /** Destination IDs where bottom nav is VISIBLE */
    private val bottomNavDestinations = setOf(
        R.id.homeFragment,
        R.id.trackingFragment,
        R.id.reportIssueFragment,
        R.id.profileFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // Restore dark mode preference before setContentView
        val prefs  = getSharedPreferences("vidya", MODE_PRIVATE)
        val isDark = prefs.getBoolean("darkMode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Wire bottom navigation
        binding.bottomNavigation.setupWithNavController(navController)

        // Show / hide bottom nav per destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigation.visibility =
                if (destination.id in bottomNavDestinations) View.VISIBLE else View.GONE
        }

        // Determine start screen
        try {
            val graph       = navController.navInflater.inflate(R.navigation.nav_graph)
            val currentUser = FirebaseAuth.getInstance().currentUser
            val hasProfile  = !prefs.getString("name", null).isNullOrEmpty()

            val startDest = when {
                currentUser != null && hasProfile  -> R.id.homeFragment
                currentUser != null && !hasProfile -> R.id.profileSetupFragment
                else                               -> R.id.onboardingFragment
            }
            graph.setStartDestination(startDest)
            navController.graph = graph
        } catch (e: Exception) {
            e.printStackTrace()
            val graph = navController.navInflater.inflate(R.navigation.nav_graph)
            graph.setStartDestination(R.id.onboardingFragment)
            navController.graph = graph
        }

        // Seed demo BMTC routes silently on first launch
        firebaseRepository.seedRoutesIfEmpty()
    }

    override fun onSupportNavigateUp(): Boolean =
        navController.navigateUp() || super.onSupportNavigateUp()
}
