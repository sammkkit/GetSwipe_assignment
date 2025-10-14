package com.samkit.swipeassignment.di

import androidx.room.Room
import com.samkit.swipeassignment.data.local.database.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
                    androidContext(),
                    AppDatabase::class.java,
                    "swipe_db"
                ).fallbackToDestructiveMigration(true).build()
    }
    single { get<AppDatabase>().productDao() }
}
