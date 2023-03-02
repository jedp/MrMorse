package com.jedparsons.mrmorse

import android.app.Application
import com.jedparsons.mrmorse.audio.KeyController
import com.jedparsons.mrmorse.audio.RealKeyController
import com.jedparsons.mrmorse.audio.RealSamplePlayer
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import logcat.AndroidLogcatLogger
import logcat.LogPriority.VERBOSE
import logcat.logcat

class MrMorseApplication : Application() {

  lateinit var appContainer: AppContainer
  private lateinit var keyController: KeyController

  init {
    System.loadLibrary("mrmorse")
  }

  override fun onCreate() {
    super.onCreate()

    AndroidLogcatLogger.installOnDebuggableApp(this, minPriority = VERBOSE)

    appContainer = RealAppContainer(this)
    keyController = RealKeyController(
      samplePlayer = RealSamplePlayer()
    )

    appContainer.demodulator.textFlow
      .onEach {
        logcat { "Decoded: $it" }
      }
      .launchIn(appContainer.mainScope)

    registerActivityLifecycleCallbacks(keyController)
  }
}