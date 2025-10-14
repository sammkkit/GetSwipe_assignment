package com.samkit.swipeassignment

import android.app.Application
import com.samkit.swipeassignment.di.databaseModule
import com.samkit.swipeassignment.di.networkModule
import com.samkit.swipeassignment.di.repositoryModule
import com.samkit.swipeassignment.di.useCaseModule
import com.samkit.swipeassignment.di.utilsModule
import com.samkit.swipeassignment.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class myApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@myApplication)
            modules(listOf(
                networkModule,
                repositoryModule,
                databaseModule,
                utilsModule,
                useCaseModule,
                viewModelModule
            ))
        }
    }
}