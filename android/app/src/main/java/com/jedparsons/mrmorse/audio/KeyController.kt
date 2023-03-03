package com.jedparsons.mrmorse.audio

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import logcat.logcat

interface KeyController : ActivityLifecycleCallbacks {

}

class RealKeyController(
  private val samplePlayer: SamplePlayer
) : KeyController {
  override fun onActivityCreated(activity: Activity, bundle: Bundle?) = Unit

  override fun onActivityStarted(activity: Activity) {
    samplePlayer.setUp(activity.assets)
  }

  override fun onActivityResumed(activity: Activity) = Unit

  override fun onActivityPaused(activity: Activity) = Unit

  override fun onActivityStopped(activity: Activity) {
    samplePlayer.tearDown()
  }

  override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) = Unit

  override fun onActivityDestroyed(activity: Activity) = Unit
}
