package com.binarypuzzle.app

import android.app.Application
import com.google.firebase.FirebaseApp

class BinaryPuzzleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
