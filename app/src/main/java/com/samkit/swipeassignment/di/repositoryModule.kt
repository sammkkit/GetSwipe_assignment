package com.samkit.swipeassignment.di

import com.samkit.swipeassignment.data.repository.ProductRepositoryImpl
import com.samkit.swipeassignment.domain.repository.ProductRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val repositoryModule = module {
    single<ProductRepository> {
        ProductRepositoryImpl(
            api = get(),
            dao = get(),
            networkHelper = get(),
            context = androidContext()
        )
    }
}