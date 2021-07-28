package cargill.com.purina

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.motion.widget.MotionLayout
import cargill.com.purina.utils.AppPreference
import cargill.com.purina.utils.Constants
import cargill.com.purina.utils.Localization

class MainActivity : AppCompatActivity() {

    lateinit var myPreference: AppPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun attachBaseContext(newBase: Context?) {
        myPreference = AppPreference(newBase!!)
        var lang: String? = myPreference.getStringValue(Constants.USER_LANGUAGE_CODE)
        super.attachBaseContext(Localization.localize(newBase, lang!!))
    }

    override fun onBackPressed() {}
}