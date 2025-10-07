package com.sumeyyaterzi.babymoments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sumeyyaterzi.babymoments.data.AppDatabase
import com.sumeyyaterzi.babymoments.data.MomentEntity
import com.sumeyyaterzi.babymoments.data.MomentRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MomentsViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = MomentRepository(AppDatabase.getInstance(app).momentDao())
    val momentsFlow = repo.getAll().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addMoment(moment: MomentEntity) {
        viewModelScope.launch { repo.insert(moment) }
    }

    fun deleteMoment(moment: MomentEntity) {
        viewModelScope.launch { repo.delete(moment) }
    }
}
