package cargill.com.purina.dashboard.Model.Home

import com.google.gson.annotations.SerializedName

class Animals (
    @SerializedName("data")
    val data: ArrayList<Animal>,
)