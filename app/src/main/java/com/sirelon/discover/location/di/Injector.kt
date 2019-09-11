package com.sirelon.discover.location.di

import android.app.Application

import com.sirelon.discover.location.network.createSimpleRetrofit
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

object Injector {

    private const val BASE_URL: String = "baseUrl"

    /**
     * Should be called only once per application start
     */
    fun init(application: Application) {
        startKoin {
            properties(mapOf(BASE_URL to "https://api.github.com/"))
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
        single { createSimpleRetrofit(androidContext(), getProperty(BASE_URL)) }

//        factory { get<Retrofit>().create(AuthAPI::class.java) }
    }
}