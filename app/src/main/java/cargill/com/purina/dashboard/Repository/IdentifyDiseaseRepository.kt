package cargill.com.purina.dashboard.Repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cargill.com.purina.Database.Event
import cargill.com.purina.Database.PurinaDAO
import cargill.com.purina.Service.PurinaApi
import cargill.com.purina.dashboard.Model.IdentifyDisease.*
import cargill.com.purina.dashboard.Model.LocateStore.StoreDetail
import cargill.com.purina.utils.AppPreference
import cargill.com.purina.utils.Constants
import retrofit2.Response

class IdentifyDiseaseRepository(
  private val dao: PurinaDAO,
  private val purinaApi: PurinaApi,
  val ctx: Context
) {
  lateinit var myPreference: AppPreference
  private var languageCode: String = ""
  private var speciedId: String = ""
  val diseaseListRemote = MutableLiveData<Response<DiseaseListResponse>>()
  val diseaseDetailsRemote = MutableLiveData<Response<DiseaseDetailResponse>>()
  val digitalVetRemote = MutableLiveData<Response<SymptomsResponse>>()
  val digitalVetDetailsRemote = MutableLiveData<Response<DiseaseResponse>>()
  val symptomsRemote = MutableLiveData<Response<SymptomsResponse>>()

  private val statusMessage = MutableLiveData<Event<String>>()
  val message: LiveData<Event<String>>
    get() = statusMessage

  init {
    myPreference = AppPreference(ctx)
    languageCode = myPreference.getStringValue(Constants.USER_LANGUAGE_CODE).toString()
    speciedId = myPreference.getStringValue(Constants.USER_ANIMAL_CODE).toString()
  }

  suspend fun getRemoteData(queryFilter: Map<String, String>) {
    val data = purinaApi.getDiseaseList(queryFilter)
    if (data.isSuccessful) {
      diseaseListRemote.value = data
    } else {
      statusMessage.value = Event("Failure")
    }
  }

  suspend fun getDiseaseDetail(diseaseId: Int) {
    val data = purinaApi.getDiseaseDetails(diseaseId)
    if (data.isSuccessful) {
      diseaseDetailsRemote.value = data
      insertDiseaseDetail(data.body()!!.DiseasesDetail)
    } else {
      statusMessage.value = Event("Failure")
    }
  }

  fun setDiseaseLocal(disease: Disease){
    return dao.insertDisease(disease)
  }

  fun getLocalDiseaseList(): List<Disease> {
    return dao.getDiseaseList(languageCode)
  }

  suspend fun insertDiseaseDetail(detail: DiseasesDetail) {
    dao.insertDiseaseDetail(detail)
  }

  fun getDiseaseDetailLocal(diseaseId: Int): DiseasesDetail{
    return dao.getDiseaseDetail(diseaseId , languageCode)
  }

  fun getLocalDiseaseSearchList(searchTxt: String): List<Disease> {
    return dao.getDiseaseSearchList(languageCode,searchTxt)
  }

  suspend fun getRemoteDigitalVetData(queryFilter: Map<String, String>) {
    val data = purinaApi.getAllSymptoms(queryFilter)
    if (data.isSuccessful) {
      digitalVetRemote.value = data
    } else {
      statusMessage.value = Event("Failure")
    }
  }

  suspend fun getRemoteDigitalVetDetailsData(queryFilter: Map<String, String>) {
    val data = purinaApi.getDigitVetDetails(queryFilter)
    if (data.isSuccessful) {
      digitalVetDetailsRemote.value = data
    } else {
      statusMessage.value = Event("Failure")
    }
  }

  suspend fun getFilteredSymptoms(queryFilter: Map<String, String>) {
    val data = purinaApi.getFilteredSymptoms(queryFilter)
    if (data.isSuccessful) {
      symptomsRemote.value = data
      if (data.body()!!.symptoms.size > 0) {
        insertSymptoms(data.body()!!.symptoms)
      }
    } else {
      statusMessage.value = Event("Failure")
    }
  }

  private suspend fun insertSymptoms(symptoms: ArrayList<Symptoms>) {
    dao.insertSymptoms(symptoms)
  }

  fun getOfflineSymptoms(): List<Symptoms> {
    return dao.getSymptomsList(languageCode,speciedId.toInt())
  }


}