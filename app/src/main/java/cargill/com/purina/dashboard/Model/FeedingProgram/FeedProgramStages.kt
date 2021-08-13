package cargill.com.purina.dashboard.Model.FeedingProgram

data class FeedProgramStages(
    val feedprogram_row: List<FeedprogramRow>,
    val mode_active: Boolean,
    val name: String,
    val species_id: Int,
    var numberOfAnimals:Int,
    var purinaFeedCost:Int,
    var otherExpenses:Int,
    var purinaFeedRequiredPerKg:Int,
    var completeFeedEquivalentKg:Int,
    var expectedMeatKg:Int,
    var conversionRateKg:Int
)