package com.vidyavahini.app.viewmodel

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

import androidx.lifecycle.viewModelScope
import com.vidyavahini.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the authentication flow.
 * Professional Grade: Uses AuthRepository and viewModelScope with Coroutines.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    /** Observed by LoginFragment and OtpFragment to drive navigation + error UI. */
    val authState = MutableLiveData<String>()

    /** Tracks whether a network call is in progress — shows/hides loading spinner. */
    val isLoading = MutableLiveData<Boolean>(false)

    // Internal: stored so verifyOtp() can construct the credential
    private var verificationId: String = ""

    // ── Email/Password Auth ──────────────────────────────────────────────────

    /**
     * Signs in a user with email and password.
     */
    fun signIn(email: String, pass: String) {
        if (email.isEmpty() || pass.isEmpty()) {
            authState.value = "error: Please enter email and password"
            return
        }
        isLoading.value = true
        viewModelScope.launch {
            try {
                authRepository.signIn(email, pass)
                authState.postValue("verified")
            } catch (e: Exception) {
                authState.postValue("error: ${e.message}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }

    /**
     * Registers a new user with email and password.
     */
    fun signUp(email: String, pass: String) {
        if (email.isEmpty() || pass.isEmpty()) {
            authState.value = "error: Please enter email and password"
            return
        }
        isLoading.value = true
        viewModelScope.launch {
            try {
                authRepository.signUp(email, pass)
                authState.postValue("verified")
            } catch (e: Exception) {
                authState.postValue("error: ${e.message}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }

    // ── OTP Request ──────────────────────────────────────────────────────────

    /**
     * Initiates phone number verification.
     * @param phoneNumber  Full international format: +91XXXXXXXXXX
     * @param activity     Required by Firebase for reCAPTCHA handling
     */
    fun sendOtp(phoneNumber: String, activity: Activity) {
        isLoading.value = true
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                // Auto-verified (some devices + Google SIM) — sign in immediately
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    isLoading.postValue(false)
                    signInWithCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    isLoading.postValue(false)
                    authState.postValue("error: ${e.message}")
                }

                override fun onCodeSent(
                    vId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    isLoading.postValue(false)
                    verificationId = vId
                    authState.postValue("otp_sent")
                }
            }).build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // ── OTP Verification ─────────────────────────────────────────────────────

    /**
     * Verifies the 6-digit OTP entered by the user.
     * @param otp  The 6-digit code from the SMS
     */
    fun verifyOtp(otp: String) {
        if (verificationId.isEmpty()) {
            authState.value = "error: Session expired. Please request OTP again."
            return
        }
        isLoading.value = true
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        signInWithCredential(credential)
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                isLoading.postValue(false)
                authState.postValue("verified")
            }
            .addOnFailureListener { e ->
                isLoading.postValue(false)
                authState.postValue("error: ${e.message}")
            }
    }

    /** True if the user is already authenticated (used by MainActivity for routing). */
    fun isLoggedIn(): Boolean = authRepository.getCurrentUser() != null

    /** Signs the user out — called from Settings or profile screen. */
    fun signOut() {
        authRepository.signOut()
    }
}
