package cargill.com.purina.dashboard.Model.IdentifyDisease

import com.google.gson.annotations.SerializedName

data class DiseaseResponse(

  var diseases: List<Diseases>

)

data class Diseases(

  @SerializedName("disease_id") var diseaseId : Int,
  @SerializedName("disease_name") var diseaseName : String,
  @SerializedName("language_code") var languageCode : String,
  @SerializedName("mode_active") var modeActive : Boolean,
  @SerializedName("count") var count : Int

)