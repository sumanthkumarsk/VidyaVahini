package com.vidyavahini.app.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.vidyavahini.app.R
import com.vidyavahini.app.data.model.OnboardingSlide
import com.vidyavahini.app.databinding.FragmentOnboardingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewPager()

        binding.btnSkip.setOnClickListener {
            navigateToSignIn()
        }
        
        binding.btnNext.setOnClickListener {
            if (binding.viewPager.currentItem + 1 < (binding.viewPager.adapter?.itemCount ?: 0)) {
                binding.viewPager.currentItem += 1
            } else {
                navigateToSignIn()
            }
        }
    }

    private fun setupViewPager() {
        val slides = listOf(
            OnboardingSlide(
                "Track Your Bus",
                "Real-time tracking of BMTC buses for your campus commute. No more waiting blindly at stops.",
                R.drawable.onboarding_track_bus
            ),
            OnboardingSlide(
                "Students Help Students",
                "Crowdsourced location updates. When one student pings the bus, everyone on the route sees it instantly.",
                R.drawable.onboarding_students_help
            ),
            OnboardingSlide(
                "Safe Reach Alerts",
                "Automatically notify your parents when you reach college safely. Peace of mind for everyone.",
                R.drawable.onboarding_safe_reach
            )
        )

        val adapter = OnboardingAdapter(slides)
        binding.viewPager.adapter = adapter

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == slides.size - 1) {
                    binding.btnNext.text = "Get Started"
                } else {
                    binding.btnNext.text = "Next"
                }
            }
        })
    }

    private fun navigateToSignIn() {
        findNavController().navigate(R.id.action_onboarding_to_signIn)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
