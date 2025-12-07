package com.sumeyyaterzi.babymoments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sumeyyaterzi.babymoments.data.MomentEntity
import com.sumeyyaterzi.babymoments.data.MomentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MomentsViewModel @Inject constructor(
    private val repo: MomentRepository
) : ViewModel() {

    val momentsFlow: StateFlow<List<MomentEntity>> = repo.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // UI State için
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun addMoment(moment: MomentEntity) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repo.insert(moment)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateMoment(moment: MomentEntity) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repo.update(moment)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteMoment(moment: MomentEntity) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repo.delete(moment)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getMomentById(id: Long): MomentEntity? {
        return momentsFlow.value.find { it.id == id.toLong() }
    }
}