package cargill.com.purina.Database

import androidx.lifecycle.LiveData
import androidx.room.*
import cargill.com.purina.dashboard.Model.Home.Animal
import cargill.com.purina.splash.Model.Country
import kotlin.collections.ArrayList

@Dao
interface PurinaDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountry(objects: ArrayList<Country>)

    @Query("SELECT * FROM country")
    fun getCountries(): LiveData<List<Country>>

    @Update
    suspend fun updateUserSelection(country: Country)

    @Query("SELECT * FROM country WHERE country_user_status = 1")
    fun getUserSelection(): LiveData<Country>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimals(objects: ArrayList<Animal>)

    @Query("SELECT * FROM animals WHERE language_code = :languageCode")
    fun getAnimals(languageCode: String): LiveData<List<Animal>>
}