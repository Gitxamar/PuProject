package cargill.com.purina.dashboard.Model.Products

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(tableName = "productCatalogue")
data class ProductCatalogue(
    @SerializedName("product_id")
    @ColumnInfo(name = "product_id")
    val id: Int,
    @SerializedName("product_name")
    @ColumnInfo(name = "product_name")
    val product_name: String,
    @SerializedName("recipe_code")
    @ColumnInfo(name = "recipe_code")
    val recipe_code: String,
    @SerializedName("image_url")
    @ColumnInfo(name = "image_url")
    val image_url: String,
)

data class ProductCatalogueList(
    //@SerializedName("data")
    val data: List<ProductCatalogue>,
)