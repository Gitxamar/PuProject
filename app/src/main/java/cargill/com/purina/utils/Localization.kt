package cargill.com.purina.utils

import android.annotation.TargetApi
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.util.*

class Localization(base: Context): ContextWrapper(base) {
    companion object{
        fun localize(ctx:Context, language: String): ContextWrapper{
            var context = ctx
            val config = context.resources.configuration
            val sysLocale: Locale?
            sysLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                getSystemLocale(config)
            }else{
                getSystemLocaleLegacy(config)
            }
            if (sysLocale != null) {
                if(language != "" && sysLocale.language != language){
                    var locale = Locale(language)
                    Locale.setDefault(locale)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                        setSystemLocale(config,locale)
                        Log.i("Language - SystemLocale",""+locale)
                    }else{
                        setSystemLocaleLegacy(config, locale)
                        Log.i("Language - SystemLocaleLegacy",""+locale)
                    }
                }
            }
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
                context = context.createConfigurationContext(config)
            }else{
                context.resources.updateConfiguration(config, context.resources.displayMetrics)
            }
            context = context.createConfigurationContext(config)
            return Localization(context)
        }

        @Suppress("DEPRECATION")
        fun getSystemLocaleLegacy(config:Configuration): Locale{
            return config.locale
        }

        @TargetApi(Build.VERSION_CODES.N)
        fun getSystemLocale(config:Configuration): Locale{
            return config.locales.get(0)
        }

        @Suppress("DEPRECATION")
        private fun setSystemLocaleLegacy(config:Configuration, locale: Locale){
            config.locale = locale
        }

        @TargetApi(Build.VERSION_CODES.N)
        private fun setSystemLocale(config:Configuration, locale: Locale){
            config.setLocale(locale)
        }
    }
}