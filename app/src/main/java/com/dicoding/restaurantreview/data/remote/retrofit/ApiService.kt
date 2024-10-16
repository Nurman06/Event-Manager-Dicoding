package com.dicoding.restaurantreview.data.remote.retrofit

import com.dicoding.restaurantreview.data.remote.response.DetailEventResponse
import com.dicoding.restaurantreview.data.remote.response.EventResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    suspend fun getEvents(
        @Query("active") active: Int,
        @Query("q") q: String? = null,
        @Query("limit") limit: Int? = null
    ): EventResponse

    @GET("events/{id}")
    suspend fun getDetailEvent(@Path("id") id: String): DetailEventResponse
}