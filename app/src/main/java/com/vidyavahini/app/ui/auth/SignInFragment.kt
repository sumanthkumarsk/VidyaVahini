package com.vidyavahini.app.ui.auth

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.vidyavahini.app.R
import com.vidyavahini.app.databinding.FragmentSignInBinding
import com.vidyavahini.app.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pass  = binding.etPassword.text.toString().trim()
            when {
                email.isEmpty() -> Toast.makeText(requireContext(), "Please enter your email", Toast.LENGTH_SHORT).show()
                pass.isEmpty()  -> Toast.makeText(requireContext(), "Please enter your password", Toast.LENGTH_SHORT).show()
                pass.length < 6 -> Toast.makeText(requireContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                else            -> viewModel.signIn(email, pass)
            }
        }

        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_signIn_to_signUp)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.btnSignIn.isEnabled = !loading
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when {
                state == "verified" -> {
                    // Check if profile is set up
                    val prefs      = requireContext().getSharedPreferences("vidya", Context.MODE_PRIVATE)
                    val hasProfile = !prefs.getString("name", null).isNullOrEmpty()
                    if (hasProfile) {
                        findNavController().navigate(R.id.action_signIn_to_home)
                    } else {
                        findNavController().navigate(R.id.action_signIn_to_home) // setup will be checked in MainActivity
                    }
                }
                state.startsWith("error") -> {
                    val raw = state.removePrefix("error: ")
                    // Show user-friendly messages instead of Firebase codes
                    val msg = when {
                        raw.contains("password")        -> "Incorrect password. Please try again."
                        raw.contains("no user record")  -> "No account found with this email."
                        raw.contains("badly formatted") -> "Please enter a valid email address."
                        raw.contains("network")         -> "No internet connection. Please check your network."
                        else                            -> "Sign-in failed. Please try again."
                    }
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
