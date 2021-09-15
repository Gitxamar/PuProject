package cargill.com.purina.dashboard.Model.IdentifyDisease

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class DiseaseDetailResponse(
  var DiseasesDetail: DiseasesDetail
)

@Entity(tableName = "DiseasesDetail")
data class DiseasesDetail(
  @ColumnInfo(name = "order_id")
  @SerializedName("order_id") var orderId: Int,
  @ColumnInfo(name = "name")
  @SerializedName("name") var name: String,
  @ColumnInfo(name = "mode_active")
  @SerializedName("mode_active") var modeActive: Boolean,
  @ColumnInfo(name = "language_code")
  @SerializedName("language_code") var languageCode: String,
  @ColumnInfo(name = "description")
  @SerializedName("description") var description: String,
  @PrimaryKey
  @ColumnInfo(name = "id")
  @SerializedName("id") var id: Int,
  @ColumnInfo(name = "symptoms_list")
  @SerializedName("symptoms_list") var symptomsList: List<SymptomsList>,
  @ColumnInfo(name = "species_list")
  @SerializedName("species_list") var speciesList: List<SpeciesList>,
  @ColumnInfo(name = "disease_images")
  @SerializedName("disease_images") var diseaseImages: List<DiseaseImageList>
)

@Parcelize
data class DiseaseImageList(
  @SerializedName("active") var active: Boolean,
  @SerializedName("disease_id") var diseaseId: Int,
  @SerializedName("image_url") var imageUrl: String,
  @SerializedName("order_id") var orderId: Int,
  @SerializedName("id") var id: Int
) : Parcelable

@Parcelize
data class SymptomsList(
  @SerializedName("language_code") var languageCode: String,
  @SerializedName("description") var description: String,
  @SerializedName("name") var name: String,
  @SerializedName("id") var id: Int,
  @SerializedName("order_id") var orderId: Int
) : Parcelable

@Parcelize
data class SpeciesList(
  @SerializedName("order_id") var orderId: Int,
  @SerializedName("id") var id: Int,
  @SerializedName("language_code") var languageCode: String,
  @SerializedName("name") var name: String
) : Parcelable

class SymptomsTypeConverter {

  @TypeConverter
  fun listToJson(value: List<SymptomsList>?) = Gson().toJson(value)

  @TypeConverter
  fun jsonToList(value: String) = Gson().fromJson(value, Array<SymptomsList>::class.java).toList()
}

class SpeciesTypeConverter {

  @TypeConverter
  fun listToJson(value: List<SpeciesList>?) = Gson().toJson(value)

  @TypeConverter
  fun jsonToList(value: String) = Gson().fromJson(value, Array<SpeciesList>::class.java).toList()
}

class DiseaseImageTypeConverter {

  @TypeConverter
  fun listToJson(value: List<DiseaseImageList>?) = Gson().toJson(value)

  @TypeConverter
  fun jsonToList(value: String) = Gson().fromJson(value, Array<DiseaseImageList>::class.java).toList()
}