package cargill.com.purina.dashboard.Model.FeedingProgram

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson

@Entity(tableName = "feedProgram")
data class FeedProgram(
    @ColumnInfo(name = "mode_active")
    val mode_active: Boolean,

    @PrimaryKey
    @ColumnInfo(name = "program_id")
    val program_id: Int,

    @ColumnInfo(name = "program_name")
    val program_name: String,

    @ColumnInfo(name = "recipe_list")
    val recipe_list: List<String>,

    @ColumnInfo(name = "species_list")
    val species_list: List<String>,


    @ColumnInfo(name = "language_code")
    val language_code: String
)

class StringTypeConverters{

    @TypeConverter
    fun listToJson(value: List<String>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) = Gson().fromJson(value, Array<String>::class.java).toList()
}