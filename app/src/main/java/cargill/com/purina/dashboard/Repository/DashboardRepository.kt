package cargill.com.purina.dashboard.Repository

import android.content.Context

import androidx.lifecycle.LiveData

import android.util.Log

import androidx.lifecycle.MutableLiveData
import cargill.com.purina.Database.Event
import cargill.com.purina.dashboard.Model.Home.Animal
import cargill.com.purina.Database.PurinaDAO
import cargill.com.purina.Service.Network
import cargill.com.purina.Service.PurinaService

import cargill.com.purina.dashboard.Model.Home.FAQs
import cargill.com.purina.dashboard.Model.Home.FaqResponse
import cargill.com.purina.dashboard.Model.IdentifyDisease.DiseaseResponse
import cargill.com.purina.dashboard.Model.LocateStore.StoreDetail
import cargill.com.purina.dashboard.Model.LocateStore.StoreImages

import cargill.com.purina.dashboard.Model.Articles.Article
import cargill.com.purina.dashboard.Model.Campaign.Campaign
import cargill.com.purina.dashboard.Model.Campaign.Campaigns

import cargill.com.purina.utils.AppPreference
import cargill.com.purina.utils.Constants
import retrofit2.Response


class DashboardRepository(private val dao: PurinaDAO, val ctx: Context) {

  val faqResponseRemote = MutableLiveData<Response<FaqResponse>>()

    lateinit var myPreference: AppPreference
    private var languageCode: String = ""
    val purinaApi = PurinaService.getDevInstance()
    val campaignData = MutableLiveData<Campaigns>()
    val campaignOfflineData = MutableLiveData<List<Campaign>>()
    val articles = MutableLiveData<List<Article>>()
    val pathWithToken = MutableLiveData<Response<String>>()


  private val statusMessage = MutableLiveData<Event<String>>()
  val message: LiveData<Event<String>>
    get() = statusMessage

  init {
    myPreference = AppPreference(ctx)
    languageCode = myPreference.getStringValue(Constants.USER_LANGUAGE_CODE).toString()
  }

  val animals = dao.getAnimals(languageCode)
  val selectedAnimal = dao.getAnimalSelected(languageCode)

  suspend fun getdata(languageCode: String) {
    val response = purinaApi.getAnimals(languageCode)
    if (response.isSuccessful()) {
      insert(response.body()!!.data)
    } else {
      onError("Error : ${response.message()}")
    }
  }

  suspend fun insert(animals: ArrayList<Animal>) {
    dao.insertAnimals(animals)
  }

  suspend fun updateAnimalSelected(animalName: String, newAnimal: Animal) {
    dao.updateAnimalSelection(newAnimal)
    if (!animalName.isEmpty()) {
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
            articles.value = dao.getArticleData(query.get(Constants.LANGUAGE).toString(), query.get(Constants.SPECIES_ID).toString())
        }
    }

    suspend fun getProductPDF(path:String){
        val data = purinaApi.getProductPDF(path)
        if(data.isSuccessful){
            pathWithToken.value = data
        }else{
            pathWithToken.value = data
        }
    }


  private fun onError(message: String) {
  }

  suspend fun getFaqRepository(queryFilter: Map<String, String>) {
    val data = purinaApi.getFAQ(queryFilter)
    if (data.isSuccessful) {
      faqResponseRemote.value = data
      if (data.body()!!.FAQs.size > 0) {
          insertFaqLocal(data.body()!!.FAQs)
      }
    } else {
      statusMessage.value = Event("Failure")
    }
  }

  private suspend fun insertFaqLocal(objects: List<FAQs>) {
    dao.insertFAQList(objects)
  }

  fun getFAQListRepository(): List<FAQs> {
    return dao.getFAQList(languageCode)
  }


}