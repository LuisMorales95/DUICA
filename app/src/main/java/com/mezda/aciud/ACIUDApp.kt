package com.mezda.aciud

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import timber.log.Timber
import timber.log.Timber.DebugTree


@HiltAndroidApp
class ACIUDApp: Application() {

    companion object {

        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()

    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }
    }

    private class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }
//            FakeCrashLibrary.log(priority, tag, message)
            if (t != null) {
                if (priority == Log.ERROR) {
//                    FakeCrashLibrary.logError(t)
                } else if (priority == Log.WARN) {
//                    FakeCrashLibrary.logWarning(t)
                }
            }
        }
    }
}