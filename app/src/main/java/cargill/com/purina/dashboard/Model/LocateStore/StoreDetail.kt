package cargill.com.purina.dashboard.Model.LocateStore

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import cargill.com.purina.dashboard.Model.ProductDetails.Image
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "StoreDetail")
data class StoreDetail(

  @ColumnInfo(name = "working_hours")
  @SerializedName("working_hours") var workingHours: String? = "-",
  @ColumnInfo(name = "language_code")
  @SerializedName("language_code") var languageCode: String,
  @ColumnInfo(name = "working_days")
  @SerializedName("working_days") var workingDays: String? = "-",
  @ColumnInfo(name = "village")
  @SerializedName("village") var village: String? = "-",
  @ColumnInfo(name = "pincode")
  @SerializedName("pincode") var pincode: Int,
  @ColumnInfo(name = "district")
  @SerializedName("district") var district: String? = "-",
  @ColumnInfo(name = "longitude")
  @SerializedName("longitude") var longitude: Double,
  @ColumnInfo(name = "dealer_name")
  @SerializedName("dealer_name") var dealerName: String? = "-",
  @ColumnInfo(name = "latitude")
  @SerializedName("latitude") var latitude: Double,
  @ColumnInfo(name = "partner_name")
  @SerializedName("partner_name") var partnerName: String? = "-",
  @ColumnInfo(name = "address")
  @SerializedName("address") var address: String? = "-",
  @ColumnInfo(name = "phone")
  @SerializedName("phone") var phone: String? = "-",
  @PrimaryKey
  @ColumnInfo(name = "id")
  @SerializedName("id") var id: Int,
  @ColumnInfo(name = "website")
  @SerializedName("website") var website: String? = "-",
  @ColumnInfo(name = "name")
  @SerializedName("name") var name: String? = "-",
  @ColumnInfo(name = "mode_active")
  @SerializedName("mode_active") var modeActive: Boolean,
  @ColumnInfo(name = "order_id")
  @SerializedName("order_id") var orderId: String? = "",
  @ColumnInfo(name = "Store_images")
  @SerializedName("Store_images") var Store_images: List<StoreImages>,
  @ColumnInfo(name = "distanceBy")
  @SerializedName("distanceBy") var distanceBy: Int,
  @ColumnInfo(name = "breeding_animals")
  @SerializedName("breeding_animals") var breeding_animals: String,
  @ColumnInfo(name = "is_freedelivery")
  @SerializedName("is_freedelivery") var is_freedelivery: String,
  @ColumnInfo(name = "is_vetservice")
  @SerializedName("is_vetservice") var is_vetservice: String? = "",
  @ColumnInfo(name = "email")
  @SerializedName("email") var email: String? = ""
)

@Parcelize
data class StoreImages(

  @SerializedName("active") var active: Boolean,
  @SerializedName("store_id") var storeId: Int,
  @SerializedName("image_url") var imageUrl: String,
  @SerializedName("order_id") var orderId: Int,
  @SerializedName("id") var id: Int

) : Parcelable

class StoreImagesTypeConverter {

  @TypeConverter
  fun listToJson(value: List<StoreImages>?) = Gson().toJson(value)

  @TypeConverter
  fun jsonToList(value: String) = Gson().fromJson(value, Array<StoreImages>::class.java).toList()
}