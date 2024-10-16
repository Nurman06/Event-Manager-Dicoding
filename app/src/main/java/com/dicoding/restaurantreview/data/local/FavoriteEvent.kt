package com.dicoding.restaurantreview.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_events")
data class FavoriteEvent(
    @PrimaryKey
    var id: String = "",
    var name: String = "",
    var mediaCover: String? = null
)