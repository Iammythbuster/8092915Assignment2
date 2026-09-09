package com.example.albumassignment

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.albumassignment.data.AlbumRepository
import com.example.albumassignment.data.ApiService
import com.example.albumassignment.di.appModule
import com.example.albumassignment.ui.dashboard.DashboardViewModel
import com.example.albumassignment.ui.login.LoginViewModel
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.koinApplication

class AppModuleTest {
    @get:Rule val instantRule = InstantTaskExecutorRule()
    @get:Rule val mainRule = MainDispatcherRule()

    @Test
    fun moduleResolvesBothViewModelsAndSharesRepository() {
        val app = koinApplication { modules(appModule) }
        try {
            val koin = app.koin
            assertNotNull(koin.get<ApiService>())
            assertNotNull(koin.get<LoginViewModel>())
            assertNotNull(koin.get<DashboardViewModel>())
            assertSame(koin.get<AlbumRepository>(), koin.get<AlbumRepository>())
        } finally {
            app.close()
        }
    }
}