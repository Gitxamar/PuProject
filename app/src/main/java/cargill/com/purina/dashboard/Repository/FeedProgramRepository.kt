package cargill.com.purina.dashboard.Repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cargill.com.purina.Database.Event
import cargill.com.purina.Database.PurinaDAO
import cargill.com.purina.Service.PurinaApi
import cargill.com.purina.dashboard.Model.FeedingProgram.FeedProgram
import cargill.com.purina.dashboard.Model.FeedingProgram.FeedingPrograms

class FeedProgramRepository(private val dao: PurinaDAO, private val purinaApi: PurinaApi, val ctx: Context) {
  val feedProgramsRemoteData = MutableLiveData<FeedingPrograms>()
  private val statusMessage= MutableLiveData<Event<String>>()
  val message: LiveData<Event<String>>
    get() = statusMessage

  suspend fun getRemotedata(query:Map<String, String>) {
    val data = purinaApi.getFeedPrograms(query)
    if(data.isSuccessful)
    {
      feedProgramsRemoteData.value = data.body()
      insertFeedProgramData(data.body()!!.FeedingPrograms)
    }else{
      statusMessage.value = Event("Failure")
    }
  }

  private suspend fun insertFeedProgramData(programs:List<FeedProgram>){
    dao.insertFeedPrograms(programs)
  }
  suspend fun getFeedProgramCacheData(languageCode:String, speciesId: String){
    val data = dao.getFeedPrograms(languageCode, speciesId)
    if(data.isNotEmpty()){
      feedProgramsRemoteData.value = FeedingPrograms(data)
    }else{
      statusMessage.value = Event("No data Found")
    }
  }
}