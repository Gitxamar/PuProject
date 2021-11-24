package cargill.com.purina.dashboard.Model.LocateStore

import com.google.gson.annotations.SerializedName

data class StoreResponse(
  @SerializedName("stores") var stores: ArrayList<Stores>,
  @SerializedName("count") var count: Int,
  @SerializedName("curr") var curr: Int,
  @SerializedName("next") var next: String,
  @SerializedName("prev") var prev: String,
  @SerializedName("lat_long") var lat_long: LatLong? = null
)

data class LatLong(@SerializedName("latitude") var latitude: Float? = 0.0f,
                   @SerializedName("longitude") var longitude: Float? = 0.0f,
                   @SerializedName("error") var error: String? = null)