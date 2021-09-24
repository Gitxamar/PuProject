package cargill.com.purina.dashboard.Model.Home

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class FaqResponse(
  @SerializedName("FAQs") var FAQs: List<FAQs>,
  @SerializedName("count") var count: Int,
  @SerializedName("curr") var curr: Int,
  @SerializedName("next") var next: String,
  @SerializedName("prev") var prev: String
)

@Entity(tableName = "FAQs")
data class FAQs(

  @ColumnInfo(name = "language_code")
  @SerializedName("language_code") var languageCode: String,
  @ColumnInfo(name = "question")
  @SerializedName("question") var question: String,
  @ColumnInfo(name = "mode_active")
  @SerializedName("mode_active") var modeActive: Boolean,
  @ColumnInfo(name = "answer")
  @SerializedName("answer") var answer: String,
  @PrimaryKey
  @ColumnInfo(name = "id")
  @SerializedName("id") var id: Int

)
