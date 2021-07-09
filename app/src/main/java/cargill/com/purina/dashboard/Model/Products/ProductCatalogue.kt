package cargill.com.purina.dashboard.Model.Products

data class ProductCatalogue(
    val count: Int,
    val curr: Int,
    val next: Int,
    val prev: Any,
    val product: ArrayList<Product>
)