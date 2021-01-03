package me.mking.currentconditions.data.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import me.mking.currentconditions.data.models.CurrentWeatherEntity

@Database(entities = [CurrentWeatherEntity::class], version = 2)
abstract class CurrentWeatherDatabase : RoomDatabase() {
    abstract fun currentWeatherDao(): CurrentWeatherDao
}
