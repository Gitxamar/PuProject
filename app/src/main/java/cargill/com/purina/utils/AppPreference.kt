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
    fun isLanguageSelected(): Boolean{
        return getStringValue(Constants.USER_LANGUAGE_CODE)!!.isNotEmpty()
    }
    fun isAnimalSelected(): Boolean{
        return getStringValue(Constants.USER_ANIMAL)!!.isNotEmpty()
    }

    fun isTermsConditionsAccepted(): Boolean{
        return getStringValue(Constants.USER_TERMS_ACCEPTED)!!.isNotEmpty()
    }

    fun isLanguageKeboardAlert(): Boolean{
        return getStringValue(Constants.IS_LOCATION_LANGUAGE_KEYBOARD)!!.isNotEmpty()
    }

    fun setNewLocationCounter(value: Int): Int{
        val number = getNewLocationCounter()!! + value
        val editor = preference.edit()
        editor.putInt("newLocation", number)
        editor.apply()
        return getNewLocationCounter()!!;
    }

    fun getNewLocationCounter() : Int{
        return preference.getInt("newLocation", 0)
    }

    fun setNewOwnerCounter(value: Int): Int{
        val number = getNewOwnerCounter()!! + value
        val editor = preference.edit()
        editor.putInt("storeDetailsUpdate", number)
        editor.apply()
        return getNewOwnerCounter()!!;
    }

    fun getNewOwnerCounter() : Int{
        return preference.getInt("storeDetailsUpdate", 0)
    }

}