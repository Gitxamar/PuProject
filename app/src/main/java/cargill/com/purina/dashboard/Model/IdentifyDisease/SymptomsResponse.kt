package cargill.com.purina.dashboard.Model.IdentifyDisease

import com.google.gson.annotations.SerializedName

data class SymptomsResponse(

  var symptoms: ArrayList<Symptoms>

)

data class Symptoms(

  @SerializedName("language_code") var languageCode: String,
  @SerializedName("description") var description: String,
  @SerializedName("name") var name: String,
  @SerializedName("id") var id: Int,
  @SerializedName("modified_by") var modifiedBy: Int,
  @SerializedName("created_by") var createdBy: Int,
  @SerializedName("order_id") var orderId: Int

)