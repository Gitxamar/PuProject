package cargill.com.purina.dashboard.Model.Campaign



data class Campaigns(
    val campaigns: List<Campaign>,
    val count: Int,
    val curr: Int,
    val next: Any,
    val prev: Any
)