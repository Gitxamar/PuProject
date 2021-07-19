package cargill.com.purina.dashboard.Model.ProductDetails

data class Image(
    val active: Boolean,
    val id: Int,
    val image_url: String,
    val order_id: Int,
    val product_id: Int
)