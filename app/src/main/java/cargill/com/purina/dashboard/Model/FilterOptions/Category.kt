package cargill.com.purina.dashboard.Model.FilterOptions

data class Category(
    val category_id: Int,
    val name: String,
    val stage: List<Stage>
)