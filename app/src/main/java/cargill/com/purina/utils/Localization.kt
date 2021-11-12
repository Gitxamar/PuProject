package cargill.com.purina.utils

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*

class Localization(base: Context): ContextWrapper(base) {
    companion object{
        fun localize(ctx:Context, language: String): ContextWrapper{
            var context = ctx
            val config = context.resources.configuration
            val sysLocale: Locale? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
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
                    }else{
                        setSystemLocaleLegacy(config, locale)
                    }
                }
            }
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
                context = context.createConfigurationContext(config)
            }else{
                context.resources.updateConfiguration(config, context.resources.displayMetrics)
            }
            return Localization(context)
        }

        fun getSystemLocaleLegacy(config:Configuration): Locale{
            return config.locale
        }

        @RequiresApi(Build.VERSION_CODES.N)
        fun getSystemLocale(config:Configuration): Locale{
            return config.locales.get(0)
        }

        private fun setSystemLocaleLegacy(config:Configuration, locale: Locale){
            config.locale = locale
        }

        private fun setSystemLocale(config:Configuration, locale: Locale){
            config.setLocale(locale)
        }
    }
}