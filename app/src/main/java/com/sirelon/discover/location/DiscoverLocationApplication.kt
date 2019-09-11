package com.sirelon.discover.location

import android.app.Application
import com.sirelon.discover.location.di.Injector


class DiscoverLocationApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Injector.init(this)
    }

}