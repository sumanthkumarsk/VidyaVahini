package com.vidyavahini.app.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.vidyavahini.app.R
import com.vidyavahini.app.databinding.FragmentSignUpBinding
import com.vidyavahini.app.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignUp.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pass  = binding.etPassword.text.toString().trim()
            when {
                email.isEmpty()            -> Toast.makeText(requireContext(), "Please enter your email", Toast.LENGTH_SHORT).show()
                !email.contains("@")       -> Toast.makeText(requireContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show()
                pass.isEmpty()             -> Toast.makeText(requireContext(), "Please enter a password", Toast.LENGTH_SHORT).show()
                pass.length < 6            -> Toast.makeText(requireContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                else                       -> viewModel.signUp(email, pass)
            }
        }

        binding.tvSignIn.setOnClickListener {
            findNavController().navigateUp()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.btnSignUp.isEnabled = !loading
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when {
                state == "verified" -> findNavController().navigate(R.id.action_signUp_to_profileSetup)
                state.startsWith("error") -> {
                    val raw = state.removePrefix("error: ")
                    val msg = when {
                        raw.contains("already in use")  -> "An account with this email already exists."
                        raw.contains("badly formatted") -> "Please enter a valid email address."
                        raw.contains("network")         -> "No internet connection."
                        else                            -> "Sign-up failed. Please try again."
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
