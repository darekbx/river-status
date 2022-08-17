package com.darekbx.riverstatus.di
import android.content.Context
import androidx.room.Room
import com.darekbx.riverstatus.repository.local.AppDatabase
import com.darekbx.riverstatus.repository.local.WaterLevelDao
import com.darekbx.riverstatus.repository.remote.BaseRiverStatusRepository
import com.darekbx.riverstatus.repository.remote.ImgwRepository
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.features.logging.*

@Module
@InstallIn(SingletonComponent::class)
class ExpensesModule {

    @Provides
    fun provideWaterLevelDao(appDatabase: AppDatabase): WaterLevelDao {
        return appDatabase.waterLevelDao()
    }

    @Provides
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder<AppDatabase>(
            appContext,
            AppDatabase::class.java,
            AppDatabase.DB_NAME
        ).build()
    }

    @Provides
    fun provideBaseRiverStatusRepository(
        httpClient: HttpClient,
        gson: Gson
    ): BaseRiverStatusRepository {
        return ImgwRepository(httpClient, gson)
    }

    @Provides
    fun provideHttpClient(): HttpClient {
        return HttpClient {
            install(Logging) {
                level = LogLevel.INFO
            }
        }
    }

    @Provides
    fun provideGson(): Gson = Gson()
}

