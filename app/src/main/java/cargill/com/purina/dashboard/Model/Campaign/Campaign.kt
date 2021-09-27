package cargill.com.purina.dashboard.Model.Campaign

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "campaign")
data class Campaign(
    val banner_image_url: String,
    val promo_image_url: String,
    @PrimaryKey
    val campaign_id: Int,
    val campaign_name: String,
    val from_date: String,
    val language_code: String,
    val mode_active: Boolean,
    val to_date: String
)