package com.dicoding.restaurantreview.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.restaurantreview.data.local.FavoriteEvent
import com.dicoding.restaurantreview.data.repository.FavoriteEventRepository

class FavoriteViewModel(repository: FavoriteEventRepository) : ViewModel() {
    val favoriteEvents: LiveData<List<FavoriteEvent>> = repository.getAllFavoriteEvents()
}