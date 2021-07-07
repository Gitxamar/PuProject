package cargill.com.purina.splash.View

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cargill.com.purina.R
import cargill.com.purina.utils.AppPreference
import cargill.com.purina.utils.Constants
import cargill.com.purina.utils.Localization

class OnboardingActivity : AppCompatActivity() {
    lateinit var myPreference: AppPreference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
    }
    override fun attachBaseContext(newBase: Context?) {
        myPreference = AppPreference(newBase!!)
        var lang: String? = myPreference.getStringValue(Constants.USER_LANGUAGE_CODE)
        super.attachBaseContext(Localization.localize(newBase, lang!!))
    }
}