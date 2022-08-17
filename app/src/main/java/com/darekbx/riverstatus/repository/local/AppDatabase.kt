package com.darekbx.riverstatus.repository.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.darekbx.riverstatus.repository.local.dtos.WaterLevelDto

@Database(entities = [WaterLevelDto::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun waterLevelDao(): WaterLevelDao

    companion object {
        val DB_NAME = "water_level"
    }
}