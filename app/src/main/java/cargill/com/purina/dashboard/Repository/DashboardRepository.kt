package cargill.com.purina.dashboard.Repository

import android.content.Context
import cargill.com.purina.dashboard.Model.Home.Animal
import cargill.com.purina.Database.PurinaDAO
import cargill.com.purina.Service.PurinaService
import cargill.com.purina.utils.AppPreference


class DashboardRepository(private val dao: PurinaDAO, val ctx: Context) {

    lateinit var myPreference: AppPreference
    private var languageCode: String = ""
    val purinaApi = PurinaService.getInstance()
    init {
        myPreference = AppPreference(ctx)
        languageCode = myPreference.getStringValue("my_language").toString()
    }
    val animals = dao.getAnimals(languageCode)

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
    private fun onError(message: String) {
    }
}