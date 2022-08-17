package com.darekbx.riverstatus.repository.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.darekbx.riverstatus.repository.local.dtos.WaterLevelDto

@Dao
interface WaterLevelDao {

    @Query("SELECT * FROM water_level ORDER BY date DESC")
    fun fetch(): List<WaterLevelDto>

    @Query("SELECT * FROM water_level ORDER BY date DESC LIMIT 1")
    fun fetchLast(): WaterLevelDto?

    @Insert
    fun insert(warterLevelDtos: List<WaterLevelDto>)
}
