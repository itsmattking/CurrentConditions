package me.mking.currentconditions.data.databases

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import me.mking.currentconditions.data.models.CurrentWeatherEntity

@Dao
interface CurrentWeatherDao {
    @Query("select * from currentweatherentity order by updated desc limit 1")
    suspend fun getCurrentWeather(): List<CurrentWeatherEntity>

    @Insert
    suspend fun insertCurrentWeather(currentWeatherEntity: CurrentWeatherEntity): Long

    @Query("delete from currentweatherentity where id != :keepId")
    suspend fun clearAllExcept(keepId: Long)
}