package cargill.com.purina.splash.Model

import com.google.gson.annotations.SerializedName

data class Language(
    @SerializedName("language_code")
    val language_code: String,
    @SerializedName("language_name")
    val language_name: String
)