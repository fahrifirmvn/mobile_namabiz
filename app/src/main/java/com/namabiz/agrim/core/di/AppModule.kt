package com.namabiz.agrim.core.di

import com.namabiz.agrim.core.database.AppDatabase
import com.namabiz.agrim.core.datastore.UserPreferencesRepository
import com.namabiz.agrim.core.network.NetworkClient
import com.namabiz.agrim.data.repository.AuthRepository
import com.namabiz.agrim.data.repository.DocumentRepository
import com.namabiz.agrim.data.repository.RfqRepository
import com.namabiz.agrim.data.repository.SupplierRepository
import com.namabiz.agrim.presentation.auth.LoginViewModel
import com.namabiz.agrim.presentation.rfq.RfqViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val databaseModule = module {
    single { AppDatabase.getInstance(get()) }
    single { get<AppDatabase>().supplierDao() }
    single { get<AppDatabase>().documentDao() }
    single { get<AppDatabase>().scanHistoryDao() }
    single { get<AppDatabase>().rfqDao() }
}

val networkModule = module {
    single { NetworkClient.getService(get()) }
    single { UserPreferencesRepository(get()) }
}

val repositoryModule = module {
    single { SupplierRepository(get()) }
    single { AuthRepository(get()) }
    single { DocumentRepository(get()) }
    single { RfqRepository(get()) }
}

val viewModelModule = module {
    viewModel { RfqViewModel(get(), get()) }
    viewModel { LoginViewModel(get(), get()) }
}

val appModules = listOf(
    databaseModule,
    networkModule,
    repositoryModule,
    viewModelModule
)
