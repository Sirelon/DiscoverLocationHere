package com.sirelon.discover.location.di

import android.app.Application
import com.sirelon.discover.location.feature.MainViewModel
import com.sirelon.discover.location.feature.map.GoogleMapInteractor
import com.sirelon.discover.location.feature.map.MapInteractor
import com.sirelon.discover.location.feature.places.PopularPlacesRepository
import com.sirelon.discover.location.feature.places.api.PlacesAPI
import com.sirelon.discover.location.feature.places.categories.CategoriesRepository

import com.sirelon.discover.location.network.createSimpleRetrofit
import com.sirelon.discover.location.utils.ColorUtils
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
                    mainModule()
                )
            )
        }
    }

    /**
     * Create module for common (shared) definitions, which can be used in any features
     */
    private fun mainModule() = module {
        single { ColorUtils(androidContext()) }

        factory<MapInteractor> { GoogleMapInteractor() }

        single { createSimpleRetrofit(androidContext(), getProperty(PLACES_BASE_URL)) }

        single { get<Retrofit>().create(PlacesAPI::class.java) }

        factory { PopularPlacesRepository(get()) }
        factory { CategoriesRepository(get()) }

        viewModel { MainViewModel(get(), get()) }
    }
}