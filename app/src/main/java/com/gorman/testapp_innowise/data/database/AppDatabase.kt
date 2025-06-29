package com.gorman.testapp_innowise.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gorman.testapp_innowise.data.models.BookmarkImage
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Database(entities = [BookmarkImage::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookmarkImageDao(): BookmarksImageDao
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "bookmark_db").build()

    @Provides
    fun provideDao(db: AppDatabase): BookmarksImageDao = db.bookmarkImageDao()
}