package cargill.com.purina.dashboard.Model.ProductDetails

data class Product(
    val audio_link: String,
    val benefits: String,
    val feeding_instructions: String,
    val form: String,
    val id: Int,
    val images: List<Image>,
    val ingredients: String,
    val language_code: String,
    val loyalty_pts: Int,
    val mixing_instructions: String,
    val mode_active: Boolean,
    val name: String,
    val nutritional_data: String,
    val pdf_link: String,
    val pkg_type: String,
    val product_details: String,
    val read_more: String,
    val recipe_code: String,
    val recommendation_for_slaughter: String,
    val sub_brand: String,
    val validity: String,
    val video_link: String
)