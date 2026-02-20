package com.dhruvathaide.drnkcontrol

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DrnkControlApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
