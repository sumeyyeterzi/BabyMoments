package com.sumeyyaterzi.babymoments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sumeyyaterzi.babymoments.data.BabyProfileEntity
import com.sumeyyaterzi.babymoments.data.BabyProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BabyProfileViewModel @Inject constructor(
    private val repository: BabyProfileRepository
) : ViewModel() {

    val profile: StateFlow<BabyProfileEntity?> = repository.getProfile()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun saveProfile(profile: BabyProfileEntity) {
        viewModelScope.launch {
            repository.insertProfile(profile)
        }
    }

    fun updateProfile(profile: BabyProfileEntity) {
        viewModelScope.launch {
            repository.updateProfile(profile)
        }
    }

    fun deleteProfile(profile: BabyProfileEntity) {
        viewModelScope.launch {
            repository.deleteProfile(profile)
        }
    }
}