package com.dicoding.restaurantreview.data.repository

import com.dicoding.restaurantreview.data.Resource
import com.dicoding.restaurantreview.data.remote.response.Event
import com.dicoding.restaurantreview.data.remote.response.ListEventsItem
import com.dicoding.restaurantreview.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class EventRepository private constructor(
    private val apiService: ApiService
) {
    suspend fun getEvents(active: Int, query: String? = null, limit: Int? = null): Flow<Resource<List<ListEventsItem>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getEvents(active, query, limit)
            emit(Resource.Success(response.listEvents))
        } catch (e: Exception) {
            emit(Resource.Error(e.toString()))
        }
    }.catch { e ->
        emit(Resource.Error(e.toString()))
    }

    suspend fun getDetailEvent(id: String): Flow<Resource<Event>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getDetailEvent(id)
            emit(Resource.Success(response.event))
        } catch (e: Exception) {
            emit(Resource.Error(e.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: EventRepository? = null
        fun getInstance(apiService: ApiService): EventRepository =
            instance ?: synchronized(this) {
                instance ?: EventRepository(apiService)
            }.also { instance = it }
    }
}