package cargill.com.purina.splash.View

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cargill.com.purina.R
import cargill.com.purina.utils.AppPreference
import cargill.com.purina.utils.Constants
import cargill.com.purina.utils.Localization
import java.util.*

class OnboardingActivity : AppCompatActivity() {
    lateinit var myPreference: AppPreference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        if(Constants.IS_PROD){
            myPreference = AppPreference(this)
            var lang: String? = "ru"
            myPreference.setStringVal(Constants.USER_LANGUAGE_CODE, "ru")
            myPreference.setStringVal(Constants.USER_LANGUAGE, "Русский")
            Localization.localize(this, lang!!)

            val config = resources.configuration
            val locale = Locale(lang)
            Locale.setDefault(locale)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                config.setLocale(locale)
            else
                config.locale = locale

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                createConfigurationContext(config)
            resources.updateConfiguration(config, resources.displayMetrics)

        }
    }
    override fun attachBaseContext(newBase: Context?) {
        myPreference = AppPreference(newBase!!)
        var lang: String? = myPreference.getStringValue(Constants.USER_LANGUAGE_CODE)
        super.attachBaseContext(Localization.localize(newBase, lang!!))
    }
}