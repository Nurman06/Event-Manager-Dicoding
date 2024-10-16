package com.dicoding.restaurantreview.data.local

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FavoriteEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favoriteEvent: FavoriteEvent)

    @Query("SELECT * FROM favorite_events")
    fun getAllFavoriteEvents(): LiveData<List<FavoriteEvent>>

    @Query("SELECT * FROM favorite_events WHERE id = :id")
    fun getFavoriteEventById(id: String): LiveData<FavoriteEvent?>

    @Delete
    suspend fun delete(favoriteEvent: FavoriteEvent)
}