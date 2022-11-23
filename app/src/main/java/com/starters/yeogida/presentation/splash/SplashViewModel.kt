package com.starters.yeogida.presentation.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    private val _isLoading = MutableLiveData(true)
    val isLoading : LiveData<Boolean> = _isLoading

    init {
        getData()
    }

    private fun getData() {
        viewModelScope.launch {
            delay(1000) // 네트워크에서 데이터 가져오는 중..
            _isLoading.value = false
        }
    }
}