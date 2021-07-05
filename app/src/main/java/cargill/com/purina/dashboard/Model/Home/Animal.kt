package cargill.com.purina.dashboard.Model.Home

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "animals")
data class Animal(
    @SerializedName("order_id")
    @ColumnInfo(name = "order_id")
    val order_id: Int,
    @PrimaryKey
    @SerializedName("id")
    @ColumnInfo(name = "animal_id")
    val id: Int,
    @SerializedName("language_code")
    @ColumnInfo(name = "language_code")
    val language_code: String,
    @SerializedName("name")
    @ColumnInfo(name = "name")
    val name: String,
    val userSelected:Int = 0
)