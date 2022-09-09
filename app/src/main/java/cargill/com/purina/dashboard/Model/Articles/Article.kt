package cargill.com.purina.dashboard.Model.Articles

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jsoup.select.Evaluator

@Entity(tableName = "article")
data class Article(
    @PrimaryKey
    val article_id: Int,
    val language_code: String,
    val mode_active: Boolean,
    val article_name: String,
    val pdf_link: String = " ",
    val url_link: String = " ",
    val species_id: Int,
    val order_id: Int,
    val thumbnail_url: String,
    val species_name: String,
    val isDownloading:Boolean
)