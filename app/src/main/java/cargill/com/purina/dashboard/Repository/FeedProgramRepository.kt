package cargill.com.purina.dashboard.Repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cargill.com.purina.Database.Event
import cargill.com.purina.Database.PurinaDAO
import cargill.com.purina.Service.PurinaApi
import cargill.com.purina.dashboard.Model.FeedingProgram.FeedProgram
import cargill.com.purina.dashboard.Model.FeedingProgram.FeedProgramStages
import cargill.com.purina.dashboard.Model.FeedingProgram.FeedingPrograms
import cargill.com.purina.dashboard.Model.FeedingProgram.FeedprogramRow

class FeedProgramRepository(private val dao: PurinaDAO, private val purinaApi: PurinaApi, val ctx: Context) {
  val feedProgramsRemoteData = MutableLiveData<FeedingPrograms>()
  val feedProgramsStageDetailsData = MutableLiveData<List<FeedprogramRow>>()
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

  suspend fun getRemoteFeedProgramStageDetails(programId:Int){
    val data = purinaApi.getFeedProgramStageDetails(programId)
    if(data.isSuccessful)
    {
      feedProgramsStageDetailsData.value = data.body()!!.FeedProgramStages.feedprogram_row
      insertFeedProgramStageDetails(data.body()!!.FeedProgramStages.feedprogram_row)
    }else{
      statusMessage.value = Event("Failure")
    }
  }

  private suspend fun insertFeedProgramStageDetails(stages:List<FeedprogramRow>){
    dao.insertFeedProgramsStageDetails(stages)
  }

  fun getFeedProgramStageCacheDetails(programId: Int){
    val data = dao.getFeedProgramStagesDetails(programId)
    if(data.isNotEmpty()){
      feedProgramsStageDetailsData.value = data
    }else{
      statusMessage.value = Event("No data Found")
    }
  }
  fun updateFeedProgramStageUnits(animalsInNumber: Int, programStage:FeedprogramRow){
    programStage.feed_cost = (programStage.feed_required * programStage.bag_price)
    programStage.accumulated_cost_kg = (programStage.feed_cost / animalsInNumber)
    programStage.accumulated_cost_head = (programStage.accumulated_cost_kg / programStage.expected_wt.toInt())
    programStage.completed_feed_equivalent = (programStage.feed_required / programStage.inclusion_rate)
    dao.updateFeedProgramStageUnits(programStage)
  }
}