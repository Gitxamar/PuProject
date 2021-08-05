package cargill.com.purina.dashboard.viewModel

import androidx.databinding.Observable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cargill.com.purina.dashboard.Repository.FeedProgramRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FeedProgramViewModel(private val repository: FeedProgramRepository): ViewModel(),
  Observable {

  val response = repository.feedProgramsRemoteData

  fun getRemoteData(queryFilter:Map<String, String>): Job =viewModelScope.launch {
    repository.getRemotedata(queryFilter)
  }

  fun getCacheData(languageCode:String, speciesId: String): Job =viewModelScope.launch {
    repository.getFeedProgramCacheData(languageCode, speciesId)
  }

  override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    TODO("Not yet implemented")
  }

  override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    TODO("Not yet implemented")
  }
}