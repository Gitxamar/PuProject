package cargill.com.purina.dashboard.Model.IdentifyDisease

import android.provider.SyncStateContract
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cargill.com.purina.utils.Constants
import com.google.gson.annotations.SerializedName

data class SymptomsResponse(

  var symptoms: ArrayList<Symptoms>

)

@Entity(tableName = "Symptoms")
data class Symptoms(

  @ColumnInfo(name = "language_code")
  @SerializedName("language_code") var languageCode: String,
  @ColumnInfo(name = "description")
  @SerializedName("description") var description: String,
  @ColumnInfo(name = "name")
  @SerializedName("name") var name: String,
  @PrimaryKey
  @ColumnInfo(name = "id")
  @SerializedName("id") var id: Int,
  @ColumnInfo(name = "modified_by")
  @SerializedName("modified_by") var modifiedBy: Int,
  @ColumnInfo(name = "created_by")
  @SerializedName("created_by") var createdBy: Int,
  @ColumnInfo(name = "order_id")
  @SerializedName("order_id") var orderId: Int,
  @ColumnInfo(name = "species_id")
  @SerializedName("species_id") var speciesid: Int

)