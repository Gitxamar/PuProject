package cargill.com.purina.utils

import android.content.Context

class AppPreference (context:Context) {
    val PREFERANCE_NAME = "AppPreference"

    val preference = context.getSharedPreferences(PREFERANCE_NAME, Context.MODE_PRIVATE)

    fun getStringValue(key: String): String? {
        return preference.getString(key, "")
    }

    fun setStringVal(key:String, value:String){
        val editor = preference.edit()
        editor.putString(key,value)
        editor.apply()
    }

}