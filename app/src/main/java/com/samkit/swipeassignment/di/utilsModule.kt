package com.samkit.swipeassignment.di

import com.samkit.swipeassignment.util.NetworkHelper
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val utilsModule = module {
    single { NetworkHelper(androidContext()) }
}