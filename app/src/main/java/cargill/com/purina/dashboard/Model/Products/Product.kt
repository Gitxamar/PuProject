package cargill.com.purina.dashboard.Model.Products

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "productcatalogues")
data class Product(
    @PrimaryKey
    @ColumnInfo(name = "product_id")
    @SerializedName("product_id")
    val product_id: Int,
    @ColumnInfo(name = "product_name")
    @SerializedName("product_name")
    val product_name: String,
    @ColumnInfo(name = "recipe_code")
    @SerializedName("recipe_code")
    val recipe_code: String,
    @ColumnInfo(name = "language_code")
    @SerializedName("language_code")
    val language_code: String,
    @ColumnInfo(name = "species_id")
    @SerializedName("species_id")
    val species_id: String,
    @ColumnInfo(name = "subspecies_id")
    @SerializedName("subspecies_id")
    val subspecies_id: String,
    @ColumnInfo(name = "category_id")
    @SerializedName("category_id")
    val category_id: String,
    @ColumnInfo(name = "stage_id")
    @SerializedName("stage_id")
    val stage_id: String,
    @ColumnInfo(name = "mode_active")
    @SerializedName("mode_active")
    val mode_active: Boolean,
    @ColumnInfo(name = "image_url")
    @SerializedName("image_url")
    val image_url: String,
)