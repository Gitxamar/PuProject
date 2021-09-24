package cargill.com.purina.dashboard.Repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cargill.com.purina.Database.Event
import cargill.com.purina.dashboard.Model.Home.Animal
import cargill.com.purina.Database.PurinaDAO
import cargill.com.purina.Service.PurinaService
import cargill.com.purina.dashboard.Model.Home.FAQs
import cargill.com.purina.dashboard.Model.Home.FaqResponse
import cargill.com.purina.dashboard.Model.IdentifyDisease.DiseaseResponse
import cargill.com.purina.dashboard.Model.LocateStore.StoreDetail
import cargill.com.purina.dashboard.Model.LocateStore.StoreImages
import cargill.com.purina.utils.AppPreference
import cargill.com.purina.utils.Constants
import retrofit2.Response


class DashboardRepository(private val dao: PurinaDAO, val ctx: Context) {

  lateinit var myPreference: AppPreference
  private var languageCode: String = ""
  val purinaApi = PurinaService.getDevInstance()
  val faqResponseRemote = MutableLiveData<Response<FaqResponse>>()

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