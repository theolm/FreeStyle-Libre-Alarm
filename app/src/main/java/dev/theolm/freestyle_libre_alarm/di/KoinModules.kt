package dev.theolm.freestyle_libre_alarm.di

import dev.theolm.freestyle_libre_alarm.data.local.database.LibreDatabase
import dev.theolm.freestyle_libre_alarm.data.local.datastore.SettingsDataStore
import dev.theolm.freestyle_libre_alarm.data.repository.AlarmRepositoryImpl
import dev.theolm.freestyle_libre_alarm.data.repository.GlucoseAlertRepositoryImpl
import dev.theolm.freestyle_libre_alarm.data.repository.SettingsRepositoryImpl
import dev.theolm.freestyle_libre_alarm.data.repository.UpdateRepositoryImpl
import dev.theolm.freestyle_libre_alarm.domain.repository.AlarmRepository
import dev.theolm.freestyle_libre_alarm.domain.repository.GlucoseAlertRepository
import dev.theolm.freestyle_libre_alarm.domain.repository.SettingsRepository
import dev.theolm.freestyle_libre_alarm.domain.repository.UpdateRepository
import dev.theolm.freestyle_libre_alarm.domain.usecase.ShouldShowUpdate
import dev.theolm.freestyle_libre_alarm.presentation.viewmodel.HistoryViewModel
import dev.theolm.freestyle_libre_alarm.presentation.viewmodel.MonitoringViewModel
import dev.theolm.freestyle_libre_alarm.presentation.viewmodel.SettingsViewModel
import dev.theolm.freestyle_libre_alarm.presentation.viewmodel.UpdateViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { SettingsDataStore(androidContext()) }

    single { LibreDatabase.getDatabase(androidContext()) }

    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<GlucoseAlertRepository> { GlucoseAlertRepositoryImpl(get()) }
    single<AlarmRepository> { AlarmRepositoryImpl(get()) }
    single<UpdateRepository> { UpdateRepositoryImpl(androidContext()) }

    factory { ShouldShowUpdate(get()) }
}

val viewModelModule = module {
    viewModel { SettingsViewModel(get()) }
    viewModel { HistoryViewModel(get()) }
    viewModel { MonitoringViewModel(androidContext(), get()) }
    viewModel { UpdateViewModel(get(), get(), get()) }
}
