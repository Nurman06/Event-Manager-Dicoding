package com.dicoding.restaurantreview.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.restaurantreview.data.local.FavoriteEvent
import com.dicoding.restaurantreview.data.repository.FavoriteEventRepository
import kotlinx.coroutines.launch

class FavoriteEventViewModel(private val repository: FavoriteEventRepository) : ViewModel() {

    fun insert(favoriteEvent: FavoriteEvent) = viewModelScope.launch {
        Log.d("FavoriteEventViewModel", "Inserting: $favoriteEvent")
        try {
            repository.insert(favoriteEvent)
            Log.d("FavoriteEventViewModel", "Insert successful")
        } catch (e: Exception) {
            Log.e("FavoriteEventViewModel", "Error inserting favorite event", e)
        }
    }

    fun delete(favoriteEvent: FavoriteEvent) = viewModelScope.launch {
        Log.d("FavoriteEventViewModel", "Deleting: $favoriteEvent")
        try {
            repository.delete(favoriteEvent)
            Log.d("FavoriteEventViewModel", "Delete successful")
        } catch (e: Exception) {
            Log.e("FavoriteEventViewModel", "Error deleting favorite event", e)
        }
    }

    fun getFavoriteEventById(id: String): LiveData<FavoriteEvent?> {
        Log.d("FavoriteEventViewModel", "Getting favorite event by id: $id")
        return repository.getFavoriteEventById(id)
    }
}