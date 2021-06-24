package cargill.com.purina.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.util.*
import kotlin.collections.ArrayList

@Dao
interface DAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountry(objects: ArrayList<Country>)

    @Query("SELECT * FROM country")
    fun getCountries(): LiveData<List<Country>>
}