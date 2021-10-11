package cargill.com.purina

import android.app.Activity
import android.app.Application
import android.content.pm.ActivityInfo
import android.os.Bundle

class PurinaApplication: Application() {
  override fun onCreate() {
    super.onCreate()
    registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks{
      override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
      }

      override fun onActivityStarted(activity: Activity) {
        TODO("Not yet implemented")
      }

      override fun onActivityResumed(activity: Activity) {
        TODO("Not yet implemented")
      }

      override fun onActivityPaused(activity: Activity) {
        TODO("Not yet implemented")
      }

      override fun onActivityStopped(activity: Activity) {
        TODO("Not yet implemented")
      }

      override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        TODO("Not yet implemented")
      }

      override fun onActivityDestroyed(activity: Activity) {
        TODO("Not yet implemented")
      }

    })
  }
}