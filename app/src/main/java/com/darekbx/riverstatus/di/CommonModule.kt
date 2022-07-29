package com.darekbx.riverstatus.di
import com.darekbx.riverstatus.repository.BaseRiverStatusRepository
import com.darekbx.riverstatus.repository.ImgwRepository
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.features.logging.*

@Module
@InstallIn(SingletonComponent::class)
class ExpensesModule {

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

