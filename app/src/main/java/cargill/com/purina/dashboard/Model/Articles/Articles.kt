package cargill.com.purina.dashboard.Model.Articles

data class Articles(
    val articles: List<Article>,
    val count: Int,
    val curr: Int,
    val next: Any,
    val prev: Any
)