package cargill.com.purina.dashboard.Model.FilterOptions

data class Category(
    @SuppressWarnings("category_id")
    val category_id: Int,
    @SuppressWarnings("name")
    val name: String,
    @SuppressWarnings("stage")
    val stage: List<Stage>
)