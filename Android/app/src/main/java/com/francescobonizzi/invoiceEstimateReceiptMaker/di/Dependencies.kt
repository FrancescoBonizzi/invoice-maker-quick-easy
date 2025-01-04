package com.francescobonizzi.invoiceEstimateReceiptMaker.di

import com.francescobonizzi.invoiceEstimateReceiptMaker.logging.DebugAndFireBaseLogger
import com.francescobonizzi.invoiceEstimateReceiptMaker.logging.ILogger
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.*
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.IAppConfigurationRepository
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.IArticlesRepository
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.ICustomersRepository
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.IInvoicesRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appDependencies = module {

    // Singleton
    single<ILogger> { DebugAndFireBaseLogger() }
    single<IAppConfigurationRepository> { JsonAppConfigurationRepository(androidContext()) }
    single<IArticlesRepository> { JsonArticlesRepository(androidContext()) }
    single<IInvoicesRepository> { JsonInvoicesRepository(androidContext()) }
    single<ICustomersRepository> { JsonCustomersRepository(androidContext()) }
    single { InMemoryInvoicePreviewBundler() }

    // Transient
    // factory { UserProfileViewModel(get(), get()) }
}