package com.dicoding.restaurantreview.ui

import android.app.Application
import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.restaurantreview.data.local.SettingPreferences
import com.dicoding.restaurantreview.data.local.dataStore
import com.dicoding.restaurantreview.data.repository.EventRepository
import com.dicoding.restaurantreview.data.repository.FavoriteEventRepository
import com.dicoding.restaurantreview.di.Injection
import com.dicoding.restaurantreview.ui.detail.DetailEventViewModel
import com.dicoding.restaurantreview.ui.favorite.FavoriteViewModel
import com.dicoding.restaurantreview.ui.finished.FinishedViewModel
import com.dicoding.restaurantreview.ui.home.HomeViewModel
import com.dicoding.restaurantreview.ui.settings.SettingsViewModel
import com.dicoding.restaurantreview.ui.upcoming.UpcomingViewModel

class ViewModelFactory private constructor(
    private val eventRepository: EventRepository,
    private val favoriteEventRepository: FavoriteEventRepository,
    private val settingPreferences: SettingPreferences
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(eventRepository) as T
            }
            modelClass.isAssignableFrom(UpcomingViewModel::class.java) -> {
                UpcomingViewModel(eventRepository) as T
            }
            modelClass.isAssignableFrom(FinishedViewModel::class.java) -> {
                FinishedViewModel(eventRepository) as T
            }
            modelClass.isAssignableFrom(DetailEventViewModel::class.java) -> {
                DetailEventViewModel(eventRepository) as T
            }
            modelClass.isAssignableFrom(FavoriteEventViewModel::class.java) -> {
                FavoriteEventViewModel(favoriteEventRepository) as T
            }
            modelClass.isAssignableFrom(FavoriteViewModel::class.java) -> {
                FavoriteViewModel(favoriteEventRepository) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(settingPreferences) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                val applicationContext = when (context) {
                    is Application -> context
                    is FragmentActivity -> context.applicationContext
                    else -> context.applicationContext
                }
                val eventRepository = Injection.provideRepository(applicationContext)
                val favoriteEventRepository = Injection.provideFavoriteRepository(applicationContext)
                val settingPreferences = SettingPreferences.getInstance(applicationContext.dataStore)
                ViewModelFactory(eventRepository, favoriteEventRepository, settingPreferences).also {
                    INSTANCE = it
                }
            }
        }
    }
}