package cargill.com.purina.dashboard.viewModel

import androidx.databinding.Observable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cargill.com.purina.dashboard.Model.LocateStore.StoreDetail
import cargill.com.purina.dashboard.Model.LocateStore.StoreDetailsModel
import cargill.com.purina.dashboard.Repository.LocateStoreRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.RequestBody

class LocateStoreViewModel(private val repository: LocateStoreRepository) : ViewModel(),
  Observable {

  val remoteStoreDetail = repository.storeDetailsRemote
  val remoteStoreList = repository.storesListRemote
  val remoteStoreRadial = repository.storesListRadialRemote
  val remoteEmail = repository.emailResponseRemote
  val msg = repository.message


  fun getRemoteData(queryFilter:Map<String, String>): Job =viewModelScope.launch {
    repository.getSearchLocation(queryFilter)
  }

  fun getRemoteRadialSearchData(queryFilter:Map<String, String>): Job =viewModelScope.launch {
    repository.getRadialSearchLocation(queryFilter)
  }

  fun getRemoteStoreDetail(storeId: Int): Job = viewModelScope.launch {
    repository.getStoreDetail(storeId)
  }

  fun getOfflineStoreDetail(storeId: Int): StoreDetail {
    return repository.getLocalStoreDetail(storeId)
  }

  fun getOfflineStoreList(): List<StoreDetail> {
    return repository.getLocalStoreList()
  }

  fun sendEmail(dataObject: RequestBody): Job = viewModelScope.launch {
    repository.sendEmail(dataObject)
  }

  override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    TODO("Not yet implemented")
  }

  override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    TODO("Not yet implemented")
  }
}