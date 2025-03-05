package com.tmatraining.musicapp

import android.app.Activity
import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {}

fun Context.getAppInstance(): MyApplication {
    return when (this) {
        is MyApplication -> this
        is Activity -> application as MyApplication
        else -> applicationContext as MyApplication
    }
}