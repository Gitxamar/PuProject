package cargill.com.purina.dashboard.Model.IdentifyDisease

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class DiseaseListResponse(
  @SerializedName("disease") var disease : List<Disease>,
  @SerializedName("count") var count : Int,
  @SerializedName("curr") var curr : Int,
  @SerializedName("next") var next : String,
  @SerializedName("prev") var prev : String
)

@Entity(tableName = "Disease")
data class Disease (
  @PrimaryKey
  @ColumnInfo(name = "disease_id")
  @SerializedName("disease_id") var diseaseId : Int,
  @ColumnInfo(name = "disease_name")
  @SerializedName("disease_name") var diseaseName : String,
  @ColumnInfo(name = "language_code")
  @SerializedName("language_code") var languageCode : String,
  @ColumnInfo(name = "mode_active")
  @SerializedName("mode_active") var modeActive : Boolean,
  //@ColumnInfo(name = "symptom_list")
  //@SerializedName("symptom_list") var symptomList : List<List<SymptomList>>

)

data class SymptomList (

  @SerializedName("language_code") var languageCode : String,
  @SerializedName("description") var description : String,
  @SerializedName("name") var name : String,
  @SerializedName("id") var id : Int,
  @SerializedName("order_id") var orderId : Int

)
