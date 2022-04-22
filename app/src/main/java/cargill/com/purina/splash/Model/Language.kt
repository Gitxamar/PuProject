package cargill.com.purina.splash.Model

import com.google.gson.annotations.SerializedName

data class Language(
    @SerializedName("language_code")
    val language_code: String,
    @SerializedName("language_name")
    val language_name: String,
    @SerializedName("mode_active")
    var modeActive : Boolean? = false,
    @SerializedName("language_image_url")
    var languageImageUrl                                                                                         : String?  = null

)