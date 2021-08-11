package cargill.com.purina.dashboard.viewModel

import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cargill.com.purina.dashboard.Model.FeedingProgram.FeedProgramStages
import cargill.com.purina.dashboard.Model.FeedingProgram.FeedprogramRow
import cargill.com.purina.dashboard.Repository.FeedProgramRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FeedProgramViewModel(private val repository: FeedProgramRepository): ViewModel(),
  Observable {

  val response = repository.feedProgramsRemoteData
  var stageResponse = MutableLiveData<List<FeedprogramRow>>()

  fun stageData():LiveData<List<FeedprogramRow>>{
    stageResponse = repository.feedProgramsStageDetailsData
    return stageResponse
  }

  fun getRemoteData(queryFilter:Map<String, String>): Job =viewModelScope.launch {
    repository.getRemotedata(queryFilter)
  }

  fun getCacheData(languageCode:String, speciesId: String): Job =viewModelScope.launch {
    repository.getFeedProgramCacheData(languageCode, speciesId)
  }

  fun getFeedProgramStageDetails(programId:Int): Job =viewModelScope.launch {
    repository.getRemoteFeedProgramStageDetails(programId)
  }

  fun getStageCacheData(programId: Int){
    repository.getFeedProgramStageCacheDetails(programId)
  }
  fun updateFeedProgramStageUnits(animalsInNumber: Int, programStage: FeedprogramRow){
    repository.updateFeedProgramStageUnits(animalsInNumber,programStage)
  }

  override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    TODO("Not yet implemented")
  }

  override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    TODO("Not yet implemented")
  }
}