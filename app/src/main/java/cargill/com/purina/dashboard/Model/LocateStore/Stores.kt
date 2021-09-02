package cargill.com.purina.dashboard.Model.LocateStore

import com.google.gson.annotations.SerializedName

data class Stores(
  @SerializedName("store_id") var storeId: Int,
  @SerializedName("store_name") var storeName: String,
  @SerializedName("store_village") var storeVillage: String,
  @SerializedName("store_district") var storeDistrict: String,
  @SerializedName("store_pincode") var storePincode: Int,
  @SerializedName("language_code") var languageCode: String,
  @SerializedName("latitude") var latitude: Double,
  @SerializedName("longitude") var longitude: Double,
  @SerializedName("phone") var phone: String,
  @SerializedName("mode_active") var modeActive: Boolean,
  @SerializedName("image_url") var imageUrl: String,
  @SerializedName("distanceBy") var distanceBy: Int
)

data class LocationDetails(val city: String) {

}

