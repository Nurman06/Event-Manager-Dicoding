package com.dicoding.restaurantreview.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.restaurantreview.data.Resource
import com.dicoding.restaurantreview.data.repository.EventRepository
import com.dicoding.restaurantreview.data.remote.response.ListEventsItem
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: EventRepository) : ViewModel() {
    private val _upcomingEvents = MutableLiveData<Resource<List<ListEventsItem>>>()
    val upcomingEvents: LiveData<Resource<List<ListEventsItem>>> = _upcomingEvents

    private val _finishedEvents = MutableLiveData<Resource<List<ListEventsItem>>>()
    val finishedEvents: LiveData<Resource<List<ListEventsItem>>> = _finishedEvents

    init {
        fetchUpcomingEvents()
        fetchFinishedEvents()
    }

    private fun fetchUpcomingEvents() {
        viewModelScope.launch {
            repository.getEvents(active = 1).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _upcomingEvents.value = Resource.Success(resource.data?.take(5) ?: emptyList())
                    }
                    else -> _upcomingEvents.value = resource
                }
            }
        }
    }

    private fun fetchFinishedEvents() {
        viewModelScope.launch {
            repository.getEvents(active = 0).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _finishedEvents.value = Resource.Success(resource.data?.take(5) ?: emptyList())
                    }
                    else -> _finishedEvents.value = resource
                }
            }
        }
    }
}