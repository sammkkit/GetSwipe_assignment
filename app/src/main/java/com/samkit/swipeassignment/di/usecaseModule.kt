package com.samkit.swipeassignment.di
import com.samkit.swipeassignment.domain.usecase.AddProductUseCase
import com.samkit.swipeassignment.domain.usecase.GetProductsUseCase
import com.samkit.swipeassignment.domain.usecase.ObserveProductsUseCase
import com.samkit.swipeassignment.domain.usecase.SyncPendingProductsUseCase
import org.koin.dsl.module


val useCaseModule = module {
    factory { GetProductsUseCase(get()) }
    factory { AddProductUseCase(get()) }
    factory { SyncPendingProductsUseCase(get()) }
    factory { ObserveProductsUseCase(get()) }
}