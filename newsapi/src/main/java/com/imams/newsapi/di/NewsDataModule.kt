package com.imams.newsapi.di

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.imams.newsapi.BuildConfig
import com.imams.newsapi.repository.NewsRepository
import com.imams.newsapi.repository.NewsRepositoryImpl
import com.imams.newsapi.source.remote.service.NewsApiService
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NewsDataModule {

    private const val BaseURL = "https://newsapi.org/v2/"
    // todo change to Build.Config or Preference DataStore Manager
    private const val apiToken = "5e6647c81dc6436596c3b2101690720e"

    @Provides
    @Singleton
    fun retrofitClient(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        chuckInterceptor: ChuckerInterceptor,
    ) : OkHttpClient {
        val builder =  OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .pingInterval(40, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .connectTimeout(20, TimeUnit.SECONDS)
            .addInterceptor {chain ->
                val url = chain.request().url.newBuilder()
                    .addQueryParameter("apiKey", apiToken).build()
                val request = chain.request().newBuilder().url(url).build()
                return@addInterceptor chain.proceed(request)
            }
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(chuckInterceptor)
        }
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideNewsApiService(retrofit: Retrofit): NewsApiService {
        return retrofit.create(NewsApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideNewsRepository(apiService: NewsApiService): NewsRepository = NewsRepositoryImpl(apiService)

}