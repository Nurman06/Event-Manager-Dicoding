@file:Suppress("UNUSED_EXPRESSION")

package com.dicoding.restaurantreview.di

import android.app.Application
import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.dicoding.restaurantreview.data.local.AppDatabase
import com.dicoding.restaurantreview.data.remote.retrofit.ApiConfig
import com.dicoding.restaurantreview.data.repository.EventRepository
import com.dicoding.restaurantreview.data.repository.FavoriteEventRepository

object Injection {
    fun provideRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        when (context) {
            is Application -> context
            is FragmentActivity -> context.application
            else -> throw IllegalArgumentException("Unknown context type")
        }
        return EventRepository.getInstance(apiService)
    }

    fun provideFavoriteRepository(context: Context): FavoriteEventRepository {
        val application = when (context) {
            is Application -> context
            is FragmentActivity -> context.application
            else -> throw IllegalArgumentException("Unknown context type")
        }
        val database = AppDatabase.getDatabase(application)
        return FavoriteEventRepository(database.favoriteEventDao())
    }
}