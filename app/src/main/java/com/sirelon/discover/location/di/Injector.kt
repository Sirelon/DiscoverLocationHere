package com.sirelon.discover.location.di

import android.app.Application
import com.sirelon.discover.location.feature.MainViewModule
import com.sirelon.discover.location.feature.places.PlacesRepository
import com.sirelon.discover.location.feature.places.api.PlacesAPI

import com.sirelon.discover.location.network.createSimpleRetrofit
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit

object Injector {

    private const val PLACES_BASE_URL: String = "baseUrl"

    /**
     * Should be called only once per application start
     */
    fun init(application: Application) {
        startKoin {
            properties(mapOf(PLACES_BASE_URL to "https://places.demo.api.here.com/places/v1/"))
            androidLogger()
            androidContext(application)
            modules(
                listOf(
                    commonModule()
                )
            )
        }
    }

    /**
     * Create module for common (shared) definitions, which can be used in any features
     */
    private fun commonModule() = module {
        single { createSimpleRetrofit(androidContext(), getProperty(PLACES_BASE_URL)) }

        factory { get<Retrofit>().create(PlacesAPI::class.java) }

        factory { PlacesRepository(get()) }

        viewModel { MainViewModule(get()) }
    }
}