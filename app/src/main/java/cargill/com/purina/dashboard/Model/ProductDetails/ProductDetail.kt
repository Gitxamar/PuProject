package cargill.com.purina.dashboard.Model.ProductDetails

import android.os.Parcelable
import androidx.room.*
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "productDetail")
data class ProductDetail(
    @ColumnInfo(name = "audio_link")
    @SerializedName("audio_link")
    val audio_link: String,
    @ColumnInfo(name = "benefits")
    @SerializedName("benefits")
    val benefits: String,
    @ColumnInfo(name = "feeding_instructions")
    @SerializedName("feeding_instructions")
    val feeding_instructions: String,
    @ColumnInfo(name = "form")
    @SerializedName("form")
    val form: String,
    @PrimaryKey
    @ColumnInfo(name = "product_id")
    @SerializedName("id")
    val id: Int,
    @ColumnInfo(name = "images")
    @SerializedName("images")
    val images: List<Image>,
    @ColumnInfo(name = "ingredients")
    @SerializedName("ingredients")
    val ingredients: String,
    @ColumnInfo(name = "language_code")
    @SerializedName("language_code")
    val language_code: String,
    @ColumnInfo(name = "loyalty_pts")
    @SerializedName("loyalty_pts")
    val loyalty_pts: Double,
    @ColumnInfo(name = "mixing_instructions")
    @SerializedName("mixing_instructions")
    val mixing_instructions: String,
    @ColumnInfo(name = "mode_active")
    @SerializedName("mode_active")
    val mode_active: Boolean,
    @ColumnInfo(name = "name")
    @SerializedName("name")
    val name: String,
    @ColumnInfo(name = "nutritional_data")
    @SerializedName("nutritional_data")
    val nutritional_data: String,
    @ColumnInfo(name = "pdf_link")
    @SerializedName("pdf_link")
    val pdf_link: String,
    @ColumnInfo(name = "pkg_type")
    @SerializedName("pkg_type")
    val pkg_type: String,
    @ColumnInfo(name = "product_details")
    @SerializedName("product_details")
    val product_details: String,
    @ColumnInfo(name = "read_more")
    @SerializedName("read_more")
    val read_more: String,
    @ColumnInfo(name = "recipe_code")
    @SerializedName("recipe_code")
    val recipe_code: String,
    @ColumnInfo(name = "recommendation_for_slaughter")
    @SerializedName("recommendation_for_slaughter")
    val recommendation_for_slaughter: String,
    @ColumnInfo(name = "sub_brand")
    @SerializedName("sub_brand")
    val sub_brand: String,
    @ColumnInfo(name = "validity")
    @SerializedName("validity")
    val validity: String,
    @ColumnInfo(name = "video_link")
    @SerializedName("video_link")
    val video_link: String
)

@Parcelize
data class Image(
    val active: Boolean,
    val id: Int,
    val image_url: String,
    val order_id: Int,
    val product_id: Int
):Parcelable

class ImagesTypeConverter{

    @TypeConverter
    fun listToJson(value: List<Image>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) = Gson().fromJson(value, Array<Image>::class.java).toList()
}