package cargill.com.purina.dashboard.Model.LocateStore

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


data class StoreDetailsModel(
  @ColumnInfo(name = "working_hours")
  @SerializedName("working_hours") var workingHours: String,
  @ColumnInfo(name = "language_code")
  @SerializedName("language_code") var languageCode: String,
  @ColumnInfo(name = "working_days")
  @SerializedName("working_days") var workingDays: String,
  @ColumnInfo(name = "village")
  @SerializedName("village") var village: String,
  @ColumnInfo(name = "pincode")
  @SerializedName("pincode") var pincode: Int,
  @ColumnInfo(name = "district")
  @SerializedName("district") var district: String,
  @ColumnInfo(name = "longitude")
  @SerializedName("longitude") var longitude: Double,
  @ColumnInfo(name = "dealer_name")
  @SerializedName("dealer_name") var dealerName: String,
  @ColumnInfo(name = "latitude")
  @SerializedName("latitude") var latitude: Double,
  @ColumnInfo(name = "partner_name")
  @SerializedName("partner_name") var partnerName: String,
  @ColumnInfo(name = "address")
  @SerializedName("address") var address: String,
  @ColumnInfo(name = "phone")
  @SerializedName("phone") var phone: String,
  @PrimaryKey
  @ColumnInfo(name = "id")
  @SerializedName("id") var id: Int,
  @ColumnInfo(name = "website")
  @SerializedName("website") var website: String,
  @ColumnInfo(name = "name")
  @SerializedName("name") var name: String,
  @ColumnInfo(name = "mode_active")
  @SerializedName("mode_active") var modeActive: Boolean,
  @ColumnInfo(name = "order_id")
  @SerializedName("order_id") var orderId: String,
  @ColumnInfo(name = "imageURL")
  @SerializedName("imageURL") var imageURL: String
)