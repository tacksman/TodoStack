package com.tacksman.todomanage

import android.app.Application
import android.util.Log
import com.jakewharton.threetenabp.AndroidThreeTen

class TodoManageApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        Log.d("Application", "ThreeTen is initialized!")
    }
}