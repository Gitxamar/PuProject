package cargill.com.purina.dashboard.Repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import cargill.com.purina.Database.Event
import cargill.com.purina.dashboard.Model.Home.Animal
import cargill.com.purina.Database.PurinaDAO
import cargill.com.purina.Service.Network
import cargill.com.purina.Service.PurinaService
import cargill.com.purina.dashboard.Model.Articles.Article
import cargill.com.purina.dashboard.Model.Campaign.Campaign
import cargill.com.purina.dashboard.Model.Campaign.Campaigns
import cargill.com.purina.utils.AppPreference
import cargill.com.purina.utils.Constants


class DashboardRepository(private val dao: PurinaDAO, val ctx: Context) {

    lateinit var myPreference: AppPreference
    private var languageCode: String = ""
    val purinaApi = PurinaService.getDevInstance()
    val campaignData = MutableLiveData<Campaigns>()
    val campaignOfflineData = MutableLiveData<List<Campaign>>()
    val articles = MutableLiveData<List<Article>>()

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
    suspend fun getProductCampaignData(query: Map<String, String>){
        val response = purinaApi.getCampaignData(query)
        if(response.isSuccessful){
            campaignData.value = response.body()
            dao.insertCampaign(response.body()!!.campaigns)
        }else{
            onError("Error : ${response.message()}")
        }
    }
    suspend fun getCampignData(code:String){
        campaignOfflineData.value = dao.getCampaignData(code)
    }

    suspend fun getArticle(query: Map<String, String>){
        if(Network.isAvailable(ctx)){
            val response = purinaApi.getArticles(query)
            if (response.isSuccessful){
                articles.value = response.body()!!.articles
                dao.insertArticle(response.body()!!.articles)
            }else{
                onError("Error : ${response.message()}")
            }
        }else{
            Log.i(Constants.LANGUAGE, query.get(Constants.LANGUAGE).toString())
            articles.value = dao.getArticleData(query.get(Constants.LANGUAGE).toString())
        }
    }

    private fun onError(message: String) {
    }
}