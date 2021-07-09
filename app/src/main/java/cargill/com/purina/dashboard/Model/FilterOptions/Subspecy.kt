package cargill.com.purina.dashboard.Model.FilterOptions

data class Subspecy(
    @SuppressWarnings("category")
    val category: List<Category>,
    @SuppressWarnings("name")
    val name: String,
    @SuppressWarnings("subspecies_id")
    val subspecies_id: Int
)