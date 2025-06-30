package com.gorman.testapp_innowise.di

import android.util.Log
import com.gorman.testapp_innowise.data.api.PexelsAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import com.gorman.testapp_innowise.BuildConfig

private const val BASE_URL = "https://api.pexels.com/"
private lateinit var apiKey: String

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideApiKey(): String {
        return BuildConfig.PEXELS_API_KEY
    }

    @Provides
    fun provideAuthInterceptor(apiKey: String): Interceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("Authorization", apiKey)
            .build()
        Log.d("PEXELS", apiKey)
        chain.proceed(request)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: Interceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun providePexelsApi(retrofit: Retrofit): PexelsAPI =
        retrofit.create(PexelsAPI::class.java)
}
