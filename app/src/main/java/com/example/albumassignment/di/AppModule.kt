package com.example.albumassignment.di

import com.example.albumassignment.data.AlbumRepository
import com.example.albumassignment.data.ApiService
import com.example.albumassignment.data.NetworkAlbumRepository
import com.example.albumassignment.ui.dashboard.DashboardViewModel
import com.example.albumassignment.ui.login.LoginViewModel
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .callTimeout(90, TimeUnit.SECONDS)
            .build()
    }
    single {
        Retrofit.Builder()
            .baseUrl("https://nit3213apinew.onrender.com/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    single<ApiService> { get<Retrofit>().create(ApiService::class.java) }
    single<AlbumRepository> { NetworkAlbumRepository(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { DashboardViewModel(get()) }
}