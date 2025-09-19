package com.gorman.testapp_innowise.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.gorman.testapp_innowise.data.datasource.remote.PexelsAPI
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
import com.gorman.testapp_innowise.data.datasource.local.AppDatabase
import com.gorman.testapp_innowise.data.datasource.local.BookmarksImageDao
import com.gorman.testapp_innowise.data.repository.BookmarksRepositoryImpl
import com.gorman.testapp_innowise.data.repository.PhotoRepositoryImpl
import com.gorman.testapp_innowise.domain.repository.BookmarkRepository
import com.gorman.testapp_innowise.domain.repository.PhotoRepository
import dagger.hilt.android.qualifiers.ApplicationContext

private const val BASE_URL = "https://api.pexels.com/"

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

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

    @Provides
    @Singleton
    fun providePhotoRepository(api: PexelsAPI): PhotoRepository =
        PhotoRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideBookmarkRepository(dao: BookmarksImageDao): BookmarkRepository =
        BookmarksRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "bookmark_db")
            .fallbackToDestructiveMigration(false)
            .build()

    @Provides
    fun provideDao(db: AppDatabase): BookmarksImageDao = db.bookmarkImageDao()
}
