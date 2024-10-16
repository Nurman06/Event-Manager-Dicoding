package com.dicoding.restaurantreview.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.restaurantreview.data.Resource
import com.dicoding.restaurantreview.data.repository.EventRepository
import com.dicoding.restaurantreview.data.remote.response.Event
import kotlinx.coroutines.launch

class DetailEventViewModel(private val repository: EventRepository) : ViewModel() {
    private val _detailEvent = MutableLiveData<Resource<Event>>()
    val detailEvent: LiveData<Resource<Event>> = _detailEvent

    fun getDetailEvent(id: String) {
        viewModelScope.launch {
            repository.getDetailEvent(id).collect { _detailEvent.value = it }
        }
    }
}