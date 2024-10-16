package com.dicoding.restaurantreview.ui.upcoming

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.restaurantreview.data.Resource
import com.dicoding.restaurantreview.data.repository.EventRepository
import com.dicoding.restaurantreview.data.remote.response.ListEventsItem
import kotlinx.coroutines.launch

class UpcomingViewModel(private val repository: EventRepository) : ViewModel() {
    private val _events = MutableLiveData<Resource<List<ListEventsItem>>>()
    val events: LiveData<Resource<List<ListEventsItem>>> = _events

    init {
        getEvents()
    }

    fun getEvents() {
        viewModelScope.launch {
            repository.getEvents(active = 1).collect { _events.value = it }
        }
    }

    fun searchEvents(query: String) {
        viewModelScope.launch {
            repository.getEvents(active = 1, query = query).collect { _events.value = it }
        }
    }
}