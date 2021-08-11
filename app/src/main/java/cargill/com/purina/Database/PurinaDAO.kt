package cargill.com.purina.Database

import androidx.lifecycle.LiveData
import androidx.room.*
import cargill.com.purina.dashboard.Model.FeedingProgram.FeedProgram
import cargill.com.purina.dashboard.Model.FeedingProgram.FeedprogramRow
import cargill.com.purina.dashboard.Model.Home.Animal
import cargill.com.purina.dashboard.Model.ProductDetails.ProductDetail
import cargill.com.purina.dashboard.Model.Products.Product
import cargill.com.purina.splash.Model.Country
import kotlin.collections.ArrayList

@Dao
interface PurinaDAO {

    //Language
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountry(objects: ArrayList<Country>)

    @Query("SELECT * FROM country")
    fun getCountries(): LiveData<List<Country>>

    @Update
    suspend fun updateUserSelection(newCountry: Country)

    @Query("UPDATE country SET country_user_status = 0 WHERE country_code = :code")
    suspend fun updateOldUserSelection(code:String)

    @Query("SELECT * FROM country WHERE country_user_status = 1")
    fun getUserSelection(): LiveData<Country>

    //Animal Filter
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimals(objects: ArrayList<Animal>)

    @Query("SELECT * FROM animals WHERE language_code = :languageCode")
    fun getAnimals(languageCode: String): LiveData<List<Animal>>

    @Update
    suspend fun updateAnimalSelection(newAnimal: Animal)

    @Query("UPDATE animals SET userSelected = 0 WHERE name = :animalName")
    suspend fun updateOldAnimalSelection(animalName:String)

    @Query("SELECT * FROM animals WHERE userSelected = 1 AND language_code =:code")
    fun getAnimalSelected(code:String): LiveData<Animal>

    //Product Catalogue
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductsCatalogue(objects: List<Product>)

    @Query("SELECT * FROM productcatalogues WHERE language_code=:code AND species_id =:speciesId")
    fun getProductsCatalogue(code:String, speciesId:String): List<Product>

    //Product Detail
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductDetail(detail: ProductDetail)

    @Query("SELECT * FROM productDetail WHERE product_id =:productId")
    fun getProductDetail(productId:Int): ProductDetail

    //Feed Program
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedPrograms(objects: List<FeedProgram>)

    @Query("SELECT * FROM feedProgram WHERE language_code=:languageCode AND species_id =:speciesId")
    fun getFeedPrograms(languageCode:String, speciesId: String): List<FeedProgram>

    //Feed Program Stages details
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedProgramsStageDetails(objects: List<FeedprogramRow>)

    @Query("SELECT * FROM stages WHERE feedprogram_id=:feedprogram_id ")
    fun getFeedProgramStagesDetails(feedprogram_id: Int): List<FeedprogramRow>

    @Update
    fun updateFeedProgramStageUnits(programStage: FeedprogramRow)
}