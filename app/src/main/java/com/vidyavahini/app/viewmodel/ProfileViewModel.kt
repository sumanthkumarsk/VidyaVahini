package com.vidyavahini.app.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vidyavahini.app.data.model.Route
import com.vidyavahini.app.data.model.Student
import com.vidyavahini.app.data.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    val routes = MutableLiveData<Map<String, Route>>()
    val isLoading = MutableLiveData<Boolean>(false)
    val profileSaved = MutableLiveData<Boolean>(false)
    val error = MutableLiveData<String>()

    fun loadRoutes() {
        isLoading.value = true
        viewModelScope.launch {
            try {
                routes.postValue(firebaseRepository.getAllRoutesAsync())
            } catch (e: Exception) {
                error.postValue(e.message)
            } finally {
                isLoading.postValue(false)
            }
        }
    }

    fun saveProfile(student: Student) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                firebaseRepository.saveStudent(student)
                profileSaved.postValue(true)
            } catch (e: Exception) {
                error.postValue(e.message)
            } finally {
                isLoading.postValue(false)
            }
        }
    }
}
