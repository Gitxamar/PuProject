package cargill.com.purina

import android.app.Activity
import android.app.Application
import android.content.pm.ActivityInfo
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class PurinaApplication: Application() {

  private lateinit var firebaseAnalytics: FirebaseAnalytics

  override fun onCreate() {
    super.onCreate()

    registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks{
      override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        firebaseAnalytics = Firebase.analytics
      }

      override fun onActivityStarted(activity: Activity) {

      }

      override fun onActivityResumed(activity: Activity) {

      }

      override fun onActivityPaused(activity: Activity) {

      }

      override fun onActivityStopped(activity: Activity) {

      }

      override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

      }

      override fun onActivityDestroyed(activity: Activity) {

      }

    })
  }
}