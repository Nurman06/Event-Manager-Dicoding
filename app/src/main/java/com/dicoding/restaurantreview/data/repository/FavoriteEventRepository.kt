package com.dicoding.restaurantreview.data.repository

import androidx.lifecycle.LiveData
import com.dicoding.restaurantreview.data.local.FavoriteEvent
import com.dicoding.restaurantreview.data.local.FavoriteEventDao

class FavoriteEventRepository(private val favoriteEventDao: FavoriteEventDao) {

    suspend fun insert(favoriteEvent: FavoriteEvent) {
        favoriteEventDao.insert(favoriteEvent)
    }

    suspend fun delete(favoriteEvent: FavoriteEvent) {
        favoriteEventDao.delete(favoriteEvent)
    }

    fun getFavoriteEventById(id: String): LiveData<FavoriteEvent?> {
        return favoriteEventDao.getFavoriteEventById(id)
    }

    fun getAllFavoriteEvents(): LiveData<List<FavoriteEvent>> = favoriteEventDao.getAllFavoriteEvents()
}