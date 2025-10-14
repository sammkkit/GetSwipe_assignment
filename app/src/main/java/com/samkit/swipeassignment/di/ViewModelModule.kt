package com.samkit.swipeassignment.di

import com.samkit.swipeassignment.presentation.addProduct.AddProductViewModel
import com.samkit.swipeassignment.presentation.productList.ProductListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val viewModelModule = module {
    viewModel { ProductListViewModel(get(),get()) }
    viewModel { AddProductViewModel(get()) }
}