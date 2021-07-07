package cargill.com.purina.dashboard.Repository

import android.content.Context
import cargill.com.purina.dashboard.Model.Home.Animal
import cargill.com.purina.Database.PurinaDAO
import cargill.com.purina.Service.PurinaService
import cargill.com.purina.splash.Model.Country
import cargill.com.purina.utils.AppPreference
import cargill.com.purina.utils.Constants


class DashboardRepository(private val dao: PurinaDAO, val ctx: Context) {

    lateinit var myPreference: AppPreference
    private var languageCode: String = ""
    val purinaApi = PurinaService.getInstance()

    init {
        myPreference = AppPreference(ctx)
        languageCode = myPreference.getStringValue(Constants.USER_LANGUAGE_CODE).toString()
    }
    val animals = dao.getAnimals(languageCode)
    val selectedAnimal = dao.getAnimalSelected(languageCode)

    suspend fun getdata(languageCode:String){
        val response = purinaApi.getAnimals(languageCode)
        if(response.isSuccessful()){
            insert(response.body()!!.data)
        }else{
            onError("Error : ${response.message()}")
        }
    }

    suspend fun insert(animals: ArrayList<Animal>){
        dao.insertAnimals(animals)
    }

    suspend fun updateAnimalSelected(animalName: String, newAnimal: Animal){
        dao.updateAnimalSelection(newAnimal)
        if(!animalName.isEmpty()){
            dao.updateOldAnimalSelection(animalName)
        }
    }

    private fun onError(message: String) {
    }
}