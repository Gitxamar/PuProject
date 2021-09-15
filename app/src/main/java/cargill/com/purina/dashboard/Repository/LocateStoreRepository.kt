package cargill.com.purina.dashboard.Repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cargill.com.purina.Database.Event
import cargill.com.purina.Database.PurinaDAO
import cargill.com.purina.Service.PurinaApi
import cargill.com.purina.dashboard.Model.LocateStore.*
import cargill.com.purina.utils.AppPreference
import cargill.com.purina.utils.Constants
import retrofit2.Response

class LocateStoreRepository(
  private val dao: PurinaDAO,
  private val purinaApi: PurinaApi,
  val ctx: Context
) {
  lateinit var myPreference: AppPreference
  private var languageCode: String = ""
  private var animalCode: String = ""
  val storesListRemote = MutableLiveData<Response<StoreResponse>>()
  val storeDetailsRemote = MutableLiveData<Response<StoreDetailsResponse>>()

  private val statusMessage = MutableLiveData<Event<String>>()
  val message: LiveData<Event<String>>
    get() = statusMessage

  init {
    myPreference = AppPreference(ctx)
    languageCode = myPreference.getStringValue(Constants.USER_LANGUAGE_CODE).toString()
    animalCode = myPreference.getStringValue(Constants.USER_ANIMAL_CODE).toString()
  }

  suspend fun getSearchLocation(queryFilter: Map<String, String>) {
    val data = purinaApi.getStoreList(queryFilter)
    if (data.isSuccessful) {
      storesListRemote.value = data
    } else {
      statusMessage.value = Event("Failure")
    }
  }

  suspend fun getStoreDetail(storeId: Int) {
    val data = purinaApi.getStoreDetails(storeId)
    if (data.isSuccessful) {
      storeDetailsRemote.value = data

      // For Offline Store Details
      if (data.body()!!.StoreDetail.Store_images.size > 0) {
        insertStoreDetail(data.body()!!.StoreDetail)
      }
      else {
        data.body()!!.StoreDetail.Store_images = listOf(StoreImages(false,data.body()!!.StoreDetail.id,Constants.DEFAULT_STORE_IMG,0,0))
        insertStoreDetail(data.body()!!.StoreDetail)
      }
    } else {
      statusMessage.value = Event("Failure")
    }
  }

  suspend fun insertStoreDetail(detail: StoreDetail) {
    dao.insertStoreDetail(detail)
  }

  fun getLocalStoreDetail(storeId: Int): StoreDetail {
    return dao.getStoreDetail(storeId, languageCode)
  }

  fun getLocalStoreList(): List<StoreDetail> {
    return dao.getStoreListDetail(languageCode)
  }

}